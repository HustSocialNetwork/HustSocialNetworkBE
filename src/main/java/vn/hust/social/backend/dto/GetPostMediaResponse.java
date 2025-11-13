package vn.hust.social.backend.dto;

public record GetPostMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
