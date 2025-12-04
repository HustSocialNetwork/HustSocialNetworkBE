package vn.hust.social.backend.controller;

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
public class MediaController {
    private final MediaService mediaService;
    private final JwtUtils jwtUtils;

    @PostMapping("/upload-url")
    public ApiResponse<List<UploadMediaResponse>> getPresignedObjectUrlsForUploading (@RequestBody List<UploadMediaRequest> uploadMediaRequests, @RequestParam String bucketName, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(mediaService.getPresignedObjectUrlsForUploading(uploadMediaRequests, bucketName, email));
    }

    @PostMapping("/download-url")
    public ApiResponse<DownloadMediasResponse> getPresignedObjectUrlsForDownloading(@RequestBody DownloadMediasRequest downloadMediasRequest, @RequestParam String bucketName, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(mediaService.getPresignedObjectUrlsForDownloading(downloadMediasRequest.downloadMediaRequests(), bucketName, email));
    }

}
