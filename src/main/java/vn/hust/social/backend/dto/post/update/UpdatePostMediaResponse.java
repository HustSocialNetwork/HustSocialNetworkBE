package vn.hust.social.backend.dto.post.update;

public record UpdatePostMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
