package vn.hust.social.backend.dto;

public record LoginResponse(
        String type,
        String accessToken,
        String refreshToken,
        UserDto user
) {}
