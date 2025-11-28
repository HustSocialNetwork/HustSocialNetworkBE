package vn.hust.social.backend.service.media;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.dto.media.UrlForUploadingMediaRequest;
import vn.hust.social.backend.dto.media.PresignedUrlForUploadingResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MediaService {
    private final MinioClient minioClient;

    public MediaService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<PresignedUrlForUploadingResponse> getPresignedObjectUrlsForUploading(List<UrlForUploadingMediaRequest> urlForUploadingMediaRequests, String bucketName) {
        try {
            List<PresignedUrlForUploadingResponse> getPresignedObjectUrlsForUploadingResponse = new ArrayList<>();
            for (UrlForUploadingMediaRequest urlForUploadingMediaRequest : urlForUploadingMediaRequests) {
                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("response-content-type", "application/json");
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String fileExtention = FileNameUtils.getExtension(urlForUploadingMediaRequest.name());
                String objectKey = date + "/" + urlForUploadingMediaRequest.type() + "/" + UUID.randomUUID() + "." + fileExtention;

                String presignedUrlForUploading =  minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectKey)
                                .expiry(60 * 60 * 24)
                                .extraQueryParams(reqParams)
                                .build()
                );
                PresignedUrlForUploadingResponse presignedUrlForUploadingResponse = new PresignedUrlForUploadingResponse(objectKey, presignedUrlForUploading, "add");
                getPresignedObjectUrlsForUploadingResponse.add(presignedUrlForUploadingResponse);
            }

            return getPresignedObjectUrlsForUploadingResponse;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public List<String> getPresignedObjectUrlsForDownloading(List<String> objectKeys, String bucketName) {
        // lấy vào 1 mảng gồm các objectKey ấy
        try {
            List<String> presignedUrlsForDownloading = new ArrayList<>();
            for (String objectKey : objectKeys) {
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
            return presignedUrlsForDownloading;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}
