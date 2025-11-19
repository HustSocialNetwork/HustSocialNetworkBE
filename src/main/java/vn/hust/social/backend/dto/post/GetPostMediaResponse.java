package vn.hust.social.backend.dto.post;

public record GetPostMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
