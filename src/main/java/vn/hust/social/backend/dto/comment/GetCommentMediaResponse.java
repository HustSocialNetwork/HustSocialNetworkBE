package vn.hust.social.backend.dto.comment;

public record GetCommentMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
