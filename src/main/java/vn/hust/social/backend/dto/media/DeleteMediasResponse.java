package vn.hust.social.backend.dto.media;

import java.util.List;
import java.util.Map;

public record DeleteMediasResponse(
        List<String> failedKeys,
        Map<String, String> errors) {
}
