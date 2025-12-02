package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.dto.media.download.DownloadMediasRequest;
import vn.hust.social.backend.dto.media.download.DownloadMediasResponse;
import vn.hust.social.backend.dto.media.upload.UploadMediasRequest;
import vn.hust.social.backend.dto.media.upload.UploadMediasResponse;
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
    public ResponseEntity<List<UploadMediasResponse>> getPresignedObjectUrlsForUploading (@RequestBody List<UploadMediasRequest> uploadMediasRequests, @RequestParam String bucketName, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(mediaService.getPresignedObjectUrlsForUploading(uploadMediasRequests, bucketName, email));
    }

    @PostMapping("/download-url")
    public ResponseEntity<DownloadMediasResponse> getPresignedObjectUrlsForDownloading(@RequestBody DownloadMediasRequest downloadMediasRequest, @RequestParam String bucketName, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(mediaService.getPresignedObjectUrlsForDownloading(downloadMediasRequest.downloadMediaRequests(), bucketName, email));
    }

}
