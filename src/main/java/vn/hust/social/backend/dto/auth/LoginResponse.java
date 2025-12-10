package vn.hust.social.backend.dto.auth;

import vn.hust.social.backend.dto.UserDTO;

public record LoginResponse(
        String type,
        String accessToken,
        String refreshToken,
        UserDTO user
) {}
