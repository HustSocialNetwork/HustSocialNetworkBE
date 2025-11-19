package vn.hust.social.backend.dto.media;

public record PresignedUrlForUploadingResponse(
        String objectKey,
        String presignedUrlForUploading,
        String operation
) {
}
