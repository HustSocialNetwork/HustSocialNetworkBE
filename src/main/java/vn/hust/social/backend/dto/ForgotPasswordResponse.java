package vn.hust.social.backend.dto;

public record ForgotPasswordResponse(
        Boolean success,
        String message
) {}
