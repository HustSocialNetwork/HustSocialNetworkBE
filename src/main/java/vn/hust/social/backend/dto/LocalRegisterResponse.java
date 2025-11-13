package vn.hust.social.backend.dto;

public record LocalRegisterResponse (
    Boolean success,
    String message,
    UserDto user
) {}
