package vn.hust.social.backend.dto.post.get;

public record GetPostMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
