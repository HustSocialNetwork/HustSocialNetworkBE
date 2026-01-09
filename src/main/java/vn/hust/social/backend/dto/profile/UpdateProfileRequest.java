package vn.hust.social.backend.dto.profile;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String displayName,
        String avatarKey,
        String backgroundKey,
        String bio
) {
}
