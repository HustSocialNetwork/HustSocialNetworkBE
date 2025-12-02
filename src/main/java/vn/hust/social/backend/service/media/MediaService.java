package vn.hust.social.backend.service.media;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.dto.media.download.DownloadMediaRequest;
import vn.hust.social.backend.dto.media.download.DownloadMediasRequest;
import vn.hust.social.backend.dto.media.download.DownloadMediasResponse;
import vn.hust.social.backend.dto.media.upload.UploadMediasRequest;
import vn.hust.social.backend.dto.media.upload.UploadMediasResponse;
import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.repository.user.UserAuthRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final MinioClient minioClient;
    private final UserAuthRepository userAuthRepository;

    public List<UploadMediasResponse> getPresignedObjectUrlsForUploading(List<UploadMediasRequest> uploadMediasRequests, String bucketName, String email) {
        try {
            userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            List<UploadMediasResponse> getPresignedObjectUrlsForUploadingResponse = new ArrayList<>();
            for (UploadMediasRequest uploadMediasRequest : uploadMediasRequests) {
                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("response-content-type", "application/json");
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String fileExtension = FileNameUtils.getExtension(uploadMediasRequest.name());
                String objectKey = date + "/" + uploadMediasRequest.type() + "/" + UUID.randomUUID() + "." + fileExtension;

                String presignedUrlForUploading =  minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectKey)
                                .expiry(60 * 60 * 24)
                                .extraQueryParams(reqParams)
                                .build()
                );
                UploadMediasResponse uploadMediasResponse = new UploadMediasResponse(objectKey, presignedUrlForUploading, MediaOperation.ADD);
                getPresignedObjectUrlsForUploadingResponse.add(uploadMediasResponse);
            }

            return getPresignedObjectUrlsForUploadingResponse;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public DownloadMediasResponse getPresignedObjectUrlsForDownloading(List<DownloadMediaRequest> downloadMediaRequests, String bucketName, String email) {
        // lấy vào 1 mảng gồm các objectKey ấy
        try {
            userAuthRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            List<String> presignedUrlsForDownloading = new ArrayList<>();
            for (DownloadMediaRequest downloadMediaRequest : downloadMediaRequests) {
                String objectKey = downloadMediaRequest.objectKey();
                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("response-content-type", "application/json");

                String presignedUrlForDownloading =  minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(objectKey)
                                .expiry(60*60*24)
                                .extraQueryParams(reqParams)
                                .build()
                );
                presignedUrlsForDownloading.add(presignedUrlForDownloading);
            }
            return new DownloadMediasResponse(presignedUrlsForDownloading);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}
