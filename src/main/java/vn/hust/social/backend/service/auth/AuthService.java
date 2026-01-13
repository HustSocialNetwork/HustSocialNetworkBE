package vn.hust.social.backend.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.auth.LocalRegisterResponse;
import vn.hust.social.backend.dto.auth.LoginResponse;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.enums.user.UserRole;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.entity.user.UserAuth.AuthProvider;
import vn.hust.social.backend.exception.*;
import vn.hust.social.backend.mapper.UserAuthMapper;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
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
    private final UserAuthMapper userAuthMapper;

    @Transactional
    public LocalRegisterResponse registerLocal(String firstName, String lastName, String displayName, String email,
            String rawPassword) {
        if (userAuthRepository.existsByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email)) {
            throw new ApiException(ResponseCode.EMAIL_ALREADY_REGISTERED);
        }

        if (userRepository.existsByDisplayName(displayName)) {
            throw new ApiException(ResponseCode.DISPLAY_NAME_ALREADY_EXISTED);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(firstName, lastName, displayName, UserRole.USER);
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

        UserRole role = email.endsWith(".edu.vn") ? UserRole.STUDENT : UserRole.USER;
        User user = new User(firstName, lastName, displayName, role);
        UserAuth userAuth = new UserAuth(user, UserAuth.AuthProvider.M365, email, null);

        userRepository.save(user);
        userAuthRepository.save(userAuth);

        UserDTO userDto = userMapper.toDTO(user);

        return new LoginResponse("Bearer",
                jwtUtils.generateAccessToken(userAuth.getEmail(), userAuth.getProvider().name(), user.getRole().name()),
                jwtUtils.generateRefreshToken(userAuth.getEmail(), userAuth.getProvider().name()), userDto,
                userAuthMapper.toDTO(userAuth));
    }

    @Transactional
    public LoginResponse loginLocal(String email, String rawPassword) {
        UserAuth userAuth = userAuthRepository
                .findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

        User user = userAuth.getUser();
        UserDTO userDto = userMapper.toDTO(user);

        if (!user.isEmailVerified()) {
            emailVerificationService.sendVerificationEmail(email, user.getDisplayName());
            throw new ApiException(ResponseCode.EMAIL_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(rawPassword, userAuth.getPassword())) {
            throw new ApiException(ResponseCode.INVALID_PASSWORD);
        }

        return new LoginResponse("Bearer",
                jwtUtils.generateAccessToken(userAuth.getEmail(), userAuth.getProvider().name(), user.getRole().name()),
                jwtUtils.generateRefreshToken(userAuth.getEmail(), userAuth.getProvider().name()), userDto,
                userAuthMapper.toDTO(userAuth));
    }

    @Transactional
    public LoginResponse loginOAuth(String email) {
        UserAuth userAuth = userAuthRepository
                .findByProviderAndEmail(UserAuth.AuthProvider.M365, email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();
        UserDTO userDto = userMapper.toDTO(user);

        return new LoginResponse("Bearer",
                jwtUtils.generateAccessToken(userAuth.getEmail(), userAuth.getProvider().name(), user.getRole().name()),
                jwtUtils.generateRefreshToken(userAuth.getEmail(), userAuth.getProvider().name()), userDto,
                userAuthMapper.toDTO(userAuth));
    }

    @Transactional
    public LoginResponse handleOAuthLogin(java.util.Map<String, Object> meResponse) {
        String email = (String) meResponse.get("mail");
        if (userAuthRepository.existsByProviderAndEmail(UserAuth.AuthProvider.M365, email)) {
            return loginOAuth(email);
        } else {
            return registerOAuth(
                    (String) meResponse.get("givenName"),
                    (String) meResponse.get("surname"),
                    ((String) meResponse.get("displayName")).replaceAll("\\s+", "") + "_" +
                            new java.util.Random().ints(16, 0, 52)
                                    .mapToObj("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"::charAt)
                                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append),
                    email);
        }
    }

    public boolean existsByProviderAndEmail(UserAuth.AuthProvider authProvider, String email) {
        return userAuthRepository.existsByProviderAndEmail(authProvider, email);
    }

    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        String email;
        String providerStr;
        try {
            email = jwtUtils.extractEmail(refreshToken);
            providerStr = jwtUtils.extractProvider(refreshToken);
        } catch (Exception e) {
            throw new ApiException(ResponseCode.UNAUTHORIZED);
        }

        AuthProvider provider;
        if (providerStr == null) {
            // For backward compatibility or invalid token structure
            throw new ApiException(ResponseCode.UNAUTHORIZED);
        } else {
            try {
                provider = AuthProvider.valueOf(providerStr);
            } catch (IllegalArgumentException e) {
                throw new ApiException(ResponseCode.UNAUTHORIZED);
            }
        }

        UserAuth userAuth = userAuthRepository.findByProviderAndEmail(provider, email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

        User user = userAuth.getUser();
        UserDTO userDto = userMapper.toDTO(user);

        return new LoginResponse("Bearer",
                jwtUtils.generateAccessToken(userAuth.getEmail(), userAuth.getProvider().name(), user.getRole().name()),
                jwtUtils.generateRefreshToken(userAuth.getEmail(), userAuth.getProvider().name()),
                userDto,
                userAuthMapper.toDTO(userAuth));
    }
}
