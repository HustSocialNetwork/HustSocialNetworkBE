package vn.hust.social.backend.dto.media.download;

import java.util.List;

public record DownloadMediasResponse(
        List<String> presignedUrlsForDownloading
) {
}
