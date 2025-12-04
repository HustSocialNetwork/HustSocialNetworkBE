package vn.hust.social.backend.dto.media.upload;

import vn.hust.social.backend.entity.enums.media.MediaOperation;

public record UploadMediaResponse(
        String objectKey,
        String presignedUrlForUploading,
        MediaOperation operation
) {
}
