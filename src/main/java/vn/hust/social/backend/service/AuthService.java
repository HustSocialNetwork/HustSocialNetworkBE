package vn.hust.social.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import vn.hust.social.backend.dto.user.auth.LocalRegisterResponse;
import vn.hust.social.backend.dto.user.auth.LoginResponse;
import vn.hust.social.backend.dto.user.UserDto;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.*;
import vn.hust.social.backend.repository.UserAuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.repository.UserRepository;
import vn.hust.social.backend.security.JwtUtils;

@Slf4j
@Service
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(
            UserAuthRepository userAuthRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.userAuthRepository = userAuthRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    private LoginResponse buildLoginResponse(UserAuth auth) {
        User user = auth.getUser();

        String accessToken = jwtUtils.generateAccessToken(auth.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(auth.getEmail());

        UserDto userDto = new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getDisplayName(),
                user.getCreatedAt()
        );

        return new LoginResponse("Bearer", accessToken, refreshToken, userDto);
    }

    private LocalRegisterResponse buildLocalRegisterResponse(User user) {
        UserDto userDto = new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getDisplayName(),
                user.getCreatedAt()
        );

        return new LocalRegisterResponse(true, "Your account is registered. Please verify your email.", userDto);
    }

    @Transactional
    public LocalRegisterResponse registerLocal(String firstName, String lastName, String displayName, String email, String rawPassword) {
        if (userAuthRepository.existsByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        if (userRepository.existsByDisplayName(displayName)) {
            throw new DisplayNameAlreadyExistedException(displayName);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(firstName, lastName, displayName);
        UserAuth auth = new UserAuth(user, UserAuth.AuthProvider.LOCAL, email, encodedPassword);

        userRepository.save(user);
        userAuthRepository.save(auth);

        return buildLocalRegisterResponse(user);
    }

    @Transactional
    public LoginResponse registerOAuth(String firstName, String lastName, String displayName, String email) {
        if (userAuthRepository.existsByProviderAndEmail(UserAuth.AuthProvider.M365, email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User user = new User(firstName, lastName, displayName);
        UserAuth auth = new UserAuth(user, UserAuth.AuthProvider.M365, email, null);

        userRepository.save(user);
        userAuthRepository.save(auth);

        return buildLoginResponse(auth);
    }

    @Transactional
    public LoginResponse loginLocal(String email, String rawPassword) {
        UserAuth auth = userAuthRepository
                .findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User user = userAuthRepository.findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email).get().getUser();
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(email);
        }

        if (!passwordEncoder.matches(rawPassword, auth.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        return buildLoginResponse(auth);
    }

    @Transactional
    public LoginResponse loginOAuth(String email) {
        UserAuth auth = userAuthRepository
                .findByProviderAndEmail(UserAuth.AuthProvider.M365, email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return buildLoginResponse(auth);
    }

    public boolean existsEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }
}
