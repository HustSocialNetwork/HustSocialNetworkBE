package vn.hust.social.backend.service.block;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.BlockDTO;
import vn.hust.social.backend.dto.block.block.UserBlockResponse;
import vn.hust.social.backend.dto.block.get.UserGetBlocksResponse;
import vn.hust.social.backend.dto.block.unblock.UserUnblockResponse;
import vn.hust.social.backend.entity.block.Block;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.BlockMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.block.BlockRepository;
import vn.hust.social.backend.repository.friendship.FriendshipRepository;
import vn.hust.social.backend.repository.user.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockMapper blockMapper;

    @Transactional
    public UserBlockResponse userBlock(UUID blockedUserId, String blockerEmail) {
        UserAuth blockerUserAuth = userAuthRepository.findByEmail(blockerEmail).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User blocker = blockerUserAuth.getUser();
        User blockedUser = userRepository.getUserById(blockedUserId).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID blockerId = blocker.getId();
        blockRepository.findBlockByBlockerIdAndBlockedId(blockerId, blockedUserId)
                .ifPresent(b -> {
                    throw new ApiException(ResponseCode.USER_ALREADY_BLOCKED);
                });
        blockRepository.findBlockByBlockerIdAndBlockedId(blockedUserId, blockerId)
                .ifPresent(b -> {
                    throw new ApiException(ResponseCode.USER_ALREADY_BEEN_BLOCKED);
                });
        Block block = new Block(blocker, blockedUser);
        blockRepository.save(block);
        BlockDTO blockDTO = blockMapper.toDTO(block);
        friendshipRepository.findFriendshipsByReceiverIdAndReceiverIdOrRequesterIdAndReceiverId(blockerId, blockedUserId, blockedUserId, blockerId).ifPresent(friendshipRepository::delete);
        return new UserBlockResponse(blockDTO);
    }

    @Transactional
    public UserUnblockResponse userUnblock(UUID blockedUserId, String blockerEmail) {
        UserAuth blockerUserAuth = userAuthRepository.findByEmail(blockerEmail).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UUID blockerId = blockerUserAuth.getUser().getId();
        Block block = blockRepository.findBlockByBlockerIdAndBlockedId(blockerId, blockedUserId).orElseThrow(() -> new ApiException(ResponseCode.BLOCK_NOT_FOUND));
        BlockDTO blockDTO = blockMapper.toDTO(block);
        blockRepository.delete(block);
        return new UserUnblockResponse(blockDTO);
    }

    @Transactional
    public UserGetBlocksResponse userGetBlocks(String blockerEmail) {
        UserAuth blockerUserAuth = userAuthRepository.findByEmail(blockerEmail).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        List<Block> blocks = blockRepository.findBlocksByBlockerId(blockerUserAuth.getUser().getId());
        List<BlockDTO> blockList = blocks.stream()
                .map(blockMapper::toDTO)
                .toList();

        return new UserGetBlocksResponse(blockList);
    }
}
