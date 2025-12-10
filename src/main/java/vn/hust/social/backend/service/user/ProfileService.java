package vn.hust.social.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.ProfileDTO;
import vn.hust.social.backend.dto.profile.*;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.block.BlockRepository;
import vn.hust.social.backend.repository.user.UserRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final UserAuthRepository userAuthRepository;
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final UserMapper userMapper;

    @Transactional
    public GetMeProfileResponse getMeProfile(String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        ProfileDTO profileDTO = userMapper.toProfileDTO(user);

        return new GetMeProfileResponse(profileDTO);
    }

    @Transactional
    public GetUserProfileResponse getUserProfile(UUID userId, String email) {
        UserAuth viewerUserAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        if (blockRepository.existsByBlockerIdAndBlockedId(userId, viewerUserAuth.getUser().getId())) throw new ApiException(ResponseCode.USER_ALREADY_BEEN_BLOCKED);
        if (blockRepository.existsByBlockerIdAndBlockedId(viewerUserAuth.getUser().getId(), userId)) throw new ApiException(ResponseCode.USER_ALREADY_BLOCKED);
        ProfileDTO profileDTO = userMapper.toProfileDTO(user);

        return new GetUserProfileResponse(profileDTO);
    }

    @Transactional
    public UpdateProfileResponse updateUserProfile(UpdateProfileRequest updateProfileRequest, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        if (!updateProfileRequest.firstName().isBlank()) user.setFirstName(updateProfileRequest.firstName());
        if (!updateProfileRequest.lastName().isBlank()) user.setLastName(updateProfileRequest.lastName());
        if (!updateProfileRequest.displayName().isBlank()) user.setDisplayName(updateProfileRequest.displayName());
        if (!updateProfileRequest.avatarKey().isBlank()) user.setAvatarKey(updateProfileRequest.avatarKey());
        if (!updateProfileRequest.backgroundKey().isBlank()) user.setBackgroundKey(updateProfileRequest.backgroundKey());
        if (!updateProfileRequest.bio().isBlank()) user.setBio(updateProfileRequest.bio());
        userRepository.save(user);
        ProfileDTO profileDTO = userMapper.toProfileDTO(user);
        return new UpdateProfileResponse(profileDTO);
    }

    @Transactional
    public SearchProfilesResponse searchProfiles(String keyword, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        if (keyword.isBlank()) throw new ApiException(ResponseCode.SEARCH_PROFILE_KEYWORD_REQUIRED);
        List<User> users = userRepository.searchProfiles(keyword, userAuth.getUser().getId());

        return new SearchProfilesResponse(users.stream()
                .map(userMapper::toProfileDTO)
                .toList());
    }
}
