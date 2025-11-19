package vn.hust.social.backend.dto.user.auth;

import vn.hust.social.backend.dto.user.UserDto;

public record LoginResponse(
        String type,
        String accessToken,
        String refreshToken,
        UserDto user
) {}
