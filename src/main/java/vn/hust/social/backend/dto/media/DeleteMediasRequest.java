package vn.hust.social.backend.dto.media;

import java.util.List;

public record DeleteMediasRequest(List<String> objectKeys) {
}
