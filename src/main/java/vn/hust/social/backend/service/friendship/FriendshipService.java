package vn.hust.social.backend.service.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.FriendshipDTO;
import vn.hust.social.backend.dto.friendship.accept.FriendAcceptResponse;
import vn.hust.social.backend.dto.friendship.incoming.FriendIncomingResponse;
import vn.hust.social.backend.dto.friendship.outgoing.FriendOutgoingResponse;
import vn.hust.social.backend.dto.friendship.request.FriendRequestResponse;
import vn.hust.social.backend.dto.friendship.unfriend.FriendUnfriendResponse;
import vn.hust.social.backend.entity.enums.user.FriendshipStatus;
import vn.hust.social.backend.entity.friendship.Friendship;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.FriendshipMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.friendship.FriendshipRepository;
import vn.hust.social.backend.repository.user.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;

    @Transactional
    public FriendRequestResponse friendRequest (UUID receiverId, String requesterEmail) {
        UserAuth userAuth = userAuthRepository.findByEmail(requesterEmail).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User requester = userAuth.getUser();
        Friendship checkFriendship = friendshipRepository.findFriendshipByRequesterAndReceiverOrReceiverAndRequester(requester, receiver, receiver, requester).orElse(null);
        if (checkFriendship == null) {
            Friendship friendship = new Friendship(requester, receiver, FriendshipStatus.PENDING);
            friendshipRepository.save(friendship);
            FriendshipDTO friendshipDTO = friendshipMapper.toDTO(friendship);

            return new FriendRequestResponse(friendshipDTO);
        } else throw new ApiException(ResponseCode.FRIENDSHIP_ALREADY_EXISTED);
    }

    @Transactional
    public FriendAcceptResponse friendAccept (UUID friendRequestId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        Friendship friendship = friendshipRepository.findFriendshipById(friendRequestId).orElseThrow(() -> new ApiException(ResponseCode.FRIENDSHIP_NOT_FOUND));
        if (friendship.getReceiver().equals(userAuth.getUser())) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);
            FriendshipDTO friendshipDTO = friendshipMapper.toDTO(friendship);

            return new FriendAcceptResponse(friendshipDTO);
        } else throw new ApiException(ResponseCode.USER_NOT_IN_THIS_FRIENDSHIP);
    }

    @Transactional
    public void friendReject (UUID friendRequestId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        Friendship friendship = friendshipRepository.findFriendshipById(friendRequestId).orElseThrow(() -> new ApiException(ResponseCode.FRIENDSHIP_NOT_FOUND));
        if (friendship.getReceiver().equals(userAuth.getUser()) && friendship.getStatus().equals(FriendshipStatus.PENDING)) {
            friendshipRepository.delete(friendship);
        } else throw new ApiException(ResponseCode.USER_NOT_IN_THIS_FRIENDSHIP);
    }

    @Transactional
    public FriendUnfriendResponse friendUnfriend (UUID friendId, String email) {
        User friend = userRepository.findById(friendId).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        Friendship friendship = friendshipRepository.findFriendshipByRequesterAndReceiverOrReceiverAndRequester(user, friend, friend, user).orElseThrow(()  -> new ApiException(ResponseCode.FRIENDSHIP_NOT_FOUND));
        FriendshipDTO friendshipDTO = friendshipMapper.toDTO(friendship);

        if (friendship.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            friendshipRepository.delete(friendship);
            return new FriendUnfriendResponse(friendshipDTO);
        } else throw new ApiException(ResponseCode.FRIENDSHIP_NOT_ACCEPTED);
    }

    @Transactional
    public FriendIncomingResponse friendIncoming (String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        List<Friendship> friendships = friendshipRepository.findFriendshipsByReceiverAndStatus(user, FriendshipStatus.PENDING);
        List<FriendshipDTO> incomingRequests = friendships.stream()
                .map(friendshipMapper::toDTO)
                .toList();
        return new FriendIncomingResponse(incomingRequests);
    }

    @Transactional
    public FriendOutgoingResponse friendOutgoing (String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        List<Friendship> friendships = friendshipRepository.findFriendshipsByRequesterAndStatus(user, FriendshipStatus.PENDING);
        List<FriendshipDTO> outgoingRequests = friendships.stream()
                .map(friendshipMapper::toDTO)
                .toList();
        return new FriendOutgoingResponse(outgoingRequests);
    }

    @Transactional
    public boolean isFriend(User user1, User user2) {
        Friendship friendship = friendshipRepository.findFriendshipByRequesterAndReceiverOrReceiverAndRequester(user1, user2, user2, user1).orElseThrow(() -> new ApiException(ResponseCode.FRIENDSHIP_NOT_FOUND));

        return friendship.getStatus() == FriendshipStatus.ACCEPTED;
    }

}
