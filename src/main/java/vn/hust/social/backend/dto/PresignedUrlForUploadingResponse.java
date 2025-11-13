package vn.hust.social.backend.dto;

public record PresignedUrlForUploadingResponse(
        String objectKey,
        String presignedUrlForUploading,
        String operation
) {
}
