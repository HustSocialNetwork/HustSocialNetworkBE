package vn.hust.social.backend.service.media;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.media.download.DownloadMediaRequest;
import vn.hust.social.backend.dto.media.download.DownloadMediasResponse;
import vn.hust.social.backend.dto.media.upload.UploadMediaRequest;
import vn.hust.social.backend.dto.media.upload.UploadMediaResponse;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.auth.UserAuthRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {
    private final MinioClient minioClient;
    private final UserAuthRepository userAuthRepository;
    private final vn.hust.social.backend.repository.media.MediaRepository mediaRepository;

    public void saveMedia(java.util.UUID targetId, vn.hust.social.backend.entity.enums.media.MediaTargetType targetType,
            vn.hust.social.backend.entity.enums.media.MediaType type, String objectKey, int orderIndex) {
        vn.hust.social.backend.entity.media.Media media = new vn.hust.social.backend.entity.media.Media(targetId,
                targetType, type, objectKey, orderIndex);
        mediaRepository.saveAndFlush(media);
    }

    public void deleteMedia(String objectKey, vn.hust.social.backend.entity.enums.media.MediaTargetType targetType) {
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
        // lấy vào 1 mảng gồm các objectKey ấy
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
}
