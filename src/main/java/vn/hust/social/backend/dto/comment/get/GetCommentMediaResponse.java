package vn.hust.social.backend.dto.comment.get;

public record GetCommentMediaResponse(
        String objectKey,
        String type,
        String orderIndex,
        String presignedUrlForDownloading
) {
}
