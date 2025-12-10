package vn.hust.social.backend.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.auth.LocalRegisterResponse;
import vn.hust.social.backend.dto.auth.LoginResponse;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.*;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.repository.user.UserRepository;
import vn.hust.social.backend.security.JwtUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailVerificationService emailVerificationService;
    private final UserAuthRepository userAuthRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Transactional
    public LocalRegisterResponse registerLocal(String firstName, String lastName, String displayName, String email, String rawPassword) {
        if (userAuthRepository.existsByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email)) {
            throw new ApiException(ResponseCode.EMAIL_ALREADY_REGISTERED);
        }

        if (userRepository.existsByDisplayName(displayName)) {
            throw new ApiException(ResponseCode.DISPLAY_NAME_ALREADY_EXISTED);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(firstName, lastName, displayName);
        UserAuth userAuth = new UserAuth(user, UserAuth.AuthProvider.LOCAL, email, encodedPassword);

        userRepository.save(user);
        userAuthRepository.save(userAuth);

        UserDTO userDto = userMapper.toDTO(user);

        return new LocalRegisterResponse(userDto);
    }

    @Transactional
    public LoginResponse registerOAuth(String firstName, String lastName, String displayName, String email) {
        if (userAuthRepository.existsByProviderAndEmail(UserAuth.AuthProvider.M365, email)) {
            throw new ApiException(ResponseCode.EMAIL_ALREADY_REGISTERED);
        }

        User user = new User(firstName, lastName, displayName);
        UserAuth userAuth = new UserAuth(user, UserAuth.AuthProvider.M365, email, null);

        userRepository.save(user);
        userAuthRepository.save(userAuth);

        UserDTO userDto = userMapper.toDTO(user);

        return new LoginResponse("Bearer", jwtUtils.generateAccessToken(userAuth.getEmail()), jwtUtils.generateRefreshToken(userAuth.getEmail()), userDto);
    }

    @Transactional
    public LoginResponse loginLocal(String email, String rawPassword) {
        UserAuth userAuth = userAuthRepository
                .findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

        User user = userAuth.getUser();
        UserDTO userDto = userMapper.toDTO(user);

        if (!user.isEmailVerified()) {
            throw new ApiException(ResponseCode.EMAIL_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(rawPassword, userAuth.getPassword())) {
            throw new ApiException(ResponseCode.INVALID_PASSWORD);
        }

        return new LoginResponse("Bearer", jwtUtils.generateAccessToken(userAuth.getEmail()), jwtUtils.generateRefreshToken(userAuth.getEmail()), userDto);
    }

    @Transactional
    public LoginResponse loginOAuth(String email) {
        UserAuth userAuth = userAuthRepository
                .findByProviderAndEmail(UserAuth.AuthProvider.M365, email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        UserDTO userDto = userMapper.toDTO(user);

        return new LoginResponse("Bearer", jwtUtils.generateAccessToken(userAuth.getEmail()), jwtUtils.generateRefreshToken(userAuth.getEmail()), userDto);
    }

    public boolean existsEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }
}
