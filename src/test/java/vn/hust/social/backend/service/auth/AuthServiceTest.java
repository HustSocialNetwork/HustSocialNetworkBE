package vn.hust.social.backend.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.dto.UserAuthDTO;
import vn.hust.social.backend.dto.auth.LoginResponse;
import vn.hust.social.backend.entity.enums.user.UserRole;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.UserAuthMapper;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.user.UserRepository;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.auth.EmailVerificationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserAuthMapper userAuthMapper;
    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserAuth userAuth;

    @BeforeEach
    void setUp() {
        user = new User("Test", "User", "TestUser", UserRole.USER);
        userAuth = new UserAuth(user, UserAuth.AuthProvider.LOCAL, "test@example.com", "password");
    }

    @Test
    void refreshToken_Success() {
        String validRefreshToken = "valid_refresh_token";
        String email = "test@example.com";
        String provider = "LOCAL";

        when(jwtUtils.extractEmail(validRefreshToken)).thenReturn(email);
        when(jwtUtils.extractProvider(validRefreshToken)).thenReturn(provider);
        when(userAuthRepository.findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email))
                .thenReturn(Optional.of(userAuth));
        when(userMapper.toDTO(any(User.class)))
                .thenReturn(new UserDTO(null, null, null, null, null, null, null, null, null, false, null));
        when(userAuthMapper.toDTO(any(UserAuth.class)))
                .thenReturn(new UserAuthDTO("test@example.com", UserAuth.AuthProvider.LOCAL));
        when(jwtUtils.generateAccessToken(any(), any(), any())).thenReturn("new_access_token");
        when(jwtUtils.generateRefreshToken(any(), any())).thenReturn("new_refresh_token");

        LoginResponse response = authService.refreshToken(validRefreshToken);

        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals("new_refresh_token", response.refreshToken());
        verify(userAuthRepository).findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email);
    }

    @Test
    void refreshToken_InvalidToken_ThrowsUnauthorized() {
        String invalidToken = "invalid_token";
        when(jwtUtils.extractEmail(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        ApiException exception = assertThrows(ApiException.class, () -> authService.refreshToken(invalidToken));
        assertEquals(ResponseCode.UNAUTHORIZED, exception.getCode());
    }

    @Test
    void refreshToken_UserNotFound_ThrowsUserNotFound() {
        String validRefreshToken = "valid_refresh_token";
        String email = "test@example.com";
        String provider = "LOCAL";

        when(jwtUtils.extractEmail(validRefreshToken)).thenReturn(email);
        when(jwtUtils.extractProvider(validRefreshToken)).thenReturn(provider);
        when(userAuthRepository.findByProviderAndEmail(UserAuth.AuthProvider.LOCAL, email))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> authService.refreshToken(validRefreshToken));
        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getCode());
    }
}
