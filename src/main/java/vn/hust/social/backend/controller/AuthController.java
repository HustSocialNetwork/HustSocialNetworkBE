package vn.hust.social.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.dto.user.auth.*;
import vn.hust.social.backend.service.AuthService;
import vn.hust.social.backend.service.EmailVerificationService;
import vn.hust.social.backend.service.M365Service;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final M365Service m365Service;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService, M365Service m365Service, EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.m365Service = m365Service;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/register/local")
    public ResponseEntity<LocalRegisterResponse> registerLocal(@RequestBody @Valid LocalRegisterRequest request) {
        LocalRegisterResponse response = authService.registerLocal(
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName(),
                request.getEmail(),
                request.getPassword()
        );
        emailVerificationService.sendVerificationEmail(request.getEmail(), request.getDisplayName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/local")
    public ResponseEntity<LoginResponse> loginLocal(@RequestBody LocalLoginRequest request) {
        LoginResponse response = authService.loginLocal(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/oauth")
    public ResponseEntity<LoginResponse> loginOAuth(@RequestBody OAuthLoginRequest request) {
        Map<String, Object> meResponse = m365Service.getUserInfo(request.getAccessToken());
        String email = (String) meResponse.get("mail");

        LoginResponse response;
        if (authService.existsEmail(email)) {
            response = authService.loginOAuth(email);
        } else {
            response = authService.registerOAuth(
                    (String) meResponse.get("givenName"),
                    (String) meResponse.get("surname"),
                    (String) meResponse.get("displayName"),
                    email
            );
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = emailVerificationService.verifyEmailToken(token);
        if (verified) {
            return ResponseEntity.ok("Email verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }
    }
}
