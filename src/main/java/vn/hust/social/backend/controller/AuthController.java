package vn.hust.social.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.dto.*;
import vn.hust.social.backend.service.AuthService;
import vn.hust.social.backend.service.M365Service;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final M365Service m365Service;

    public AuthController(AuthService authService, M365Service m365Service) {
        this.authService = authService;
        this.m365Service = m365Service;
    }

    @GetMapping("/")
    public String home() {
        return "Server is running!";
    }

    @PostMapping("/register/local")
    public ResponseEntity<LoginResponse> registerLocal(@RequestBody @Valid LocalRegisterRequest request) {
        LoginResponse response = authService.registerLocal(
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName(),
                request.getEmail(),
                request.getPassword()
        );
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

//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
//
//    }
}
