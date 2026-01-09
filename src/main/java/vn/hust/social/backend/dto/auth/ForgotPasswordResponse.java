package vn.hust.social.backend.dto.auth;

public record ForgotPasswordResponse(
        Boolean success,
        String message
) {}
