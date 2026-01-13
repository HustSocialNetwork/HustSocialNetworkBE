package vn.hust.social.backend.dto.club;

public record UpdateClubRequest(
        String name,
        String description,
        String avatarKey,
        String backgroundKey) {
}
