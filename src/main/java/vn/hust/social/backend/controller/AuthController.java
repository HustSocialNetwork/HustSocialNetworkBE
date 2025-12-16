package vn.hust.social.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.auth.*;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.service.auth.AuthService;
import vn.hust.social.backend.service.auth.EmailVerificationService;
import vn.hust.social.backend.service.auth.M365Service;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final M365Service m365Service;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register/local")
    public ApiResponse<LocalRegisterResponse> registerLocal(@RequestBody @Valid LocalRegisterRequest request) {
        LocalRegisterResponse response = authService.registerLocal(
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName(),
                request.getEmail(),
                request.getPassword()
        );
        emailVerificationService.sendVerificationEmail(request.getEmail(), request.getDisplayName());
        return ApiResponse.success(response);
    }

    @PostMapping("/login/local")
    public ApiResponse<LoginResponse> loginLocal(@RequestBody LocalLoginRequest request) {
        return ApiResponse.success(authService.loginLocal(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/login/oauth")
    public ApiResponse<LoginResponse> loginOAuth(@RequestBody OAuthLoginRequest request) {
        Map<String, Object> meResponse = m365Service.getUserInfo(request.getAccessToken());
        String email = (String) meResponse.get("mail");

        LoginResponse response;
        if (authService.existsByProviderAndEmail(UserAuth.AuthProvider.M365, email)) {
            response = authService.loginOAuth(email);
        } else {
            response = authService.registerOAuth(
                    (String) meResponse.get("givenName"),
                    (String) meResponse.get("surname"),
        ((String) meResponse.get("displayName")).replaceAll("\\s+", "") + "_" +
                new java.util.Random().ints(16, 0, 52)
                        .mapToObj("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"::charAt)
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append),
                    email
            );
        }

        return ApiResponse.success(response);
    }

    @GetMapping("/verify-email")
    public ApiResponse<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = emailVerificationService.verifyEmailToken(token);
        if (verified) {
            return ApiResponse.success("Email verified successfully");
        } else {
            throw new ApiException(ResponseCode.INVALID_OR_EXPIRED_EMAIL_VERIFICATION_TOKEN);
        }
    }
}
