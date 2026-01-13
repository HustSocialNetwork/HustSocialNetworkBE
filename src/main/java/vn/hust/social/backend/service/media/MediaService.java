package vn.hust.social.backend.service.media;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.media.DeleteMediasResponse;
import vn.hust.social.backend.dto.media.download.DownloadMediaRequest;
import vn.hust.social.backend.dto.media.download.DownloadMediasResponse;
import vn.hust.social.backend.dto.media.upload.UploadMediaRequest;
import vn.hust.social.backend.dto.media.upload.UploadMediaResponse;
import vn.hust.social.backend.entity.media.Media;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.enums.media.MediaType;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.media.MediaRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {
    private final MinioClient minioClient;
    private final UserAuthRepository userAuthRepository;
    private final MediaRepository mediaRepository;

    public void saveMedia(java.util.UUID targetId, MediaTargetType targetType,
            MediaType type, String objectKey, int orderIndex) {
        Media media = new Media(targetId, targetType, type, objectKey, orderIndex);
        mediaRepository.saveAndFlush(media);
    }

    public void deleteMedia(String objectKey, MediaTargetType targetType) {
        mediaRepository.deleteByObjectKeyAndTargetType(objectKey, targetType);
    }

    public List<UploadMediaResponse> getPresignedObjectUrlsForUploading(List<UploadMediaRequest> uploadMediaRequests,
            String bucketName, String email) {
        try {
            userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
            List<UploadMediaResponse> getPresignedObjectUrlsForUploadingResponse = new ArrayList<>();
            for (UploadMediaRequest uploadMediaRequest : uploadMediaRequests) {
                Map<String, String> reqParams = new HashMap<>();
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String fileExtension = FileNameUtils.getExtension(uploadMediaRequest.name());
                String objectKey = date + "/" + uploadMediaRequest.type() + "/" + UUID.randomUUID() + "."
                        + fileExtension;

                String presignedUrlForUploading = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectKey)
                                .expiry(60 * 60 * 24)
                                .extraQueryParams(reqParams)
                                .build());
                UploadMediaResponse uploadMediaResponse = new UploadMediaResponse(objectKey, presignedUrlForUploading,
                        MediaOperation.ADD);
                getPresignedObjectUrlsForUploadingResponse.add(uploadMediaResponse);
            }

            return getPresignedObjectUrlsForUploadingResponse;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ApiException(ResponseCode.UNKNOWN_ERROR);
        }
    }

    public DownloadMediasResponse getPresignedObjectUrlsForDownloading(List<DownloadMediaRequest> downloadMediaRequests,
            String bucketName, String email) {
        try {
            userAuthRepository.findByEmail(email).orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
            List<String> presignedUrlsForDownloading = new ArrayList<>();
            for (DownloadMediaRequest downloadMediaRequest : downloadMediaRequests) {
                String objectKey = downloadMediaRequest.objectKey();
                Map<String, String> reqParams = new HashMap<>();

                String presignedUrlForDownloading = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(objectKey)
                                .expiry(60 * 60 * 24)
                                .extraQueryParams(reqParams)
                                .build());
                presignedUrlsForDownloading.add(presignedUrlForDownloading);
            }
            return new DownloadMediasResponse(presignedUrlsForDownloading);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ApiException(ResponseCode.UNKNOWN_ERROR);
        }
    }

    public DeleteMediasResponse deleteMedias(List<String> objectKeys, String bucketName) {
        try {
            List<DeleteObject> objects = new LinkedList<>();
            for (String key : objectKeys) {
                objects.add(new DeleteObject(key));
            }

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());

            List<String> failedKeys = new ArrayList<>();
            Map<String, String> errors = new HashMap<>();

            // The Iterable is lazy, so we must iterate to actually perform the deletion
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                failedKeys.add(error.objectName());
                errors.put(error.objectName(), error.message());
                log.error("Error deleting object " + error.objectName() + "; " + error.message());
            }

            return new DeleteMediasResponse(failedKeys, errors);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ApiException(ResponseCode.UNKNOWN_ERROR);
        }
    }
}
