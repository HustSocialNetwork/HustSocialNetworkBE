package vn.hust.social.backend.dto.media.download;

import java.util.List;

public record DownloadMediasRequest(
        List<DownloadMediaRequest> downloadMediaRequests
) {
}
