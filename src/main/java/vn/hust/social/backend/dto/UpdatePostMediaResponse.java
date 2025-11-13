package vn.hust.social.backend.dto;

public record UpdatePostMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
