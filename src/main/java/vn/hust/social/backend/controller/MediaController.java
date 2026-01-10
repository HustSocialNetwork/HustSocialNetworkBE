package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.media.download.DownloadMediasRequest;
import vn.hust.social.backend.dto.media.download.DownloadMediasResponse;
import vn.hust.social.backend.dto.media.upload.UploadMediaRequest;
import vn.hust.social.backend.dto.media.upload.UploadMediaResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.media.MediaService;

import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Media Upload/Download APIs")
public class MediaController {
    private final MediaService mediaService;
    private final JwtUtils jwtUtils;

    @PostMapping("/upload-url")
    @Operation(summary = "Get upload URLs", description = "Get presigned URLs for uploading media")
    public ApiResponse<List<UploadMediaResponse>> getPresignedObjectUrlsForUploading(
            @RequestBody List<UploadMediaRequest> uploadMediaRequests, @RequestParam String bucketName,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse
                .success(mediaService.getPresignedObjectUrlsForUploading(uploadMediaRequests, bucketName, email));
    }

    @PostMapping("/download-url")
    @Operation(summary = "Get download URLs", description = "Get presigned URLs for downloading media")
    public ApiResponse<DownloadMediasResponse> getPresignedObjectUrlsForDownloading(
            @RequestBody DownloadMediasRequest downloadMediasRequest, @RequestParam String bucketName,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(mediaService.getPresignedObjectUrlsForDownloading(
                downloadMediasRequest.downloadMediaRequests(), bucketName, email));
    }

}
