package vn.hust.social.backend.dto.user.auth;

import vn.hust.social.backend.dto.user.UserDto;

public record LocalRegisterResponse (
    Boolean success,
    String message,
    UserDto user
) {}
