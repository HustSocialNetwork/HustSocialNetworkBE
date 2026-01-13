package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.auth.*;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.service.auth.AuthService;
import vn.hust.social.backend.service.auth.EmailVerificationService;
import vn.hust.social.backend.service.auth.M365Service;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;
    private final M365Service m365Service;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register/local")
    @Operation(summary = "Register (Local)", description = "Register a new user with email and password")
    public ApiResponse<LocalRegisterResponse> registerLocal(@RequestBody @Valid LocalRegisterRequest request) {
        LocalRegisterResponse response = authService.registerLocal(
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName(),
                request.getEmail(),
                request.getPassword());
        emailVerificationService.sendVerificationEmail(request.getEmail(), request.getDisplayName());
        return ApiResponse.success(response);
    }

    @PostMapping("/login/local")
    @Operation(summary = "Login (Local)", description = "Login with email and password")
    public ApiResponse<LoginResponse> loginLocal(@RequestBody LocalLoginRequest request) {
        return ApiResponse.success(authService.loginLocal(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/login/oauth")
    @Operation(summary = "Login (OAuth)", description = "Login with Microsoft OAuth access token")
    public ApiResponse<LoginResponse> loginOAuth(@RequestBody OAuthLoginRequest request) {
        Map<String, Object> meResponse = m365Service.getUserInfo(request.getAccessToken());
        LoginResponse response = authService.handleOAuthLogin(meResponse);
        return ApiResponse.success(response);
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify Email", description = "Verify email using token")
    public ApiResponse<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = emailVerificationService.verifyEmailToken(token);
        if (verified) {
            return ApiResponse.success("Email verified successfully");
        } else {
            throw new ApiException(ResponseCode.INVALID_OR_EXPIRED_EMAIL_VERIFICATION_TOKEN);
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Token", description = "Get new access token using refresh token")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshToken(request.getRefreshToken()));
    }
}
