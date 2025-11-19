package vn.hust.social.backend.dto.user.auth;

public record ForgotPasswordResponse(
        Boolean success,
        String message
) {}
