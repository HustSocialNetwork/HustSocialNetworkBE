package vn.hust.social.backend.dto.user.auth;

import vn.hust.social.backend.dto.user.UserDTO;

public record LocalRegisterResponse (
    Boolean success,
    String message,
    UserDTO user
) {}
