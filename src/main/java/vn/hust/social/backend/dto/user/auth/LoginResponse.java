package vn.hust.social.backend.dto.user.auth;

import vn.hust.social.backend.dto.user.UserDTO;

public record LoginResponse(
        String type,
        String accessToken,
        String refreshToken,
        UserDTO user
) {}
