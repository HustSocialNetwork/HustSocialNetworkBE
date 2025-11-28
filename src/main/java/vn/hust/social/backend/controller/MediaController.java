package vn.hust.social.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.dto.media.UrlForUploadingMediaRequest;
import vn.hust.social.backend.dto.media.PresignedUrlForUploadingResponse;
import vn.hust.social.backend.service.media.MediaService;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload-url")
    public ResponseEntity<List<PresignedUrlForUploadingResponse>> getPresignedObjectUrlsForUploading (@RequestBody List<UrlForUploadingMediaRequest> urlForUploadingMediaRequests, @RequestParam String bucketName) {
        return ResponseEntity.ok(mediaService.getPresignedObjectUrlsForUploading(urlForUploadingMediaRequests, bucketName));
    }
}
