package vn.hust.social.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.ProfileDTO;
import vn.hust.social.backend.dto.user.profile.GetMeProfileResponse;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserAuthRepository userAuthRepository;
    private final UserMapper userMapper;

    @Transactional
    public GetMeProfileResponse getMeProfile(String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        ProfileDTO profileDTO = userMapper.toProfileDTO(user);

        return new GetMeProfileResponse(profileDTO);
    }
}
