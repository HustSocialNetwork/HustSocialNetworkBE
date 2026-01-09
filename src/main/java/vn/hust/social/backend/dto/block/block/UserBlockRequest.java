package vn.hust.social.backend.dto.block.block;

import java.util.UUID;

public record UserBlockRequest(
        UUID blockedUserId
) {
}
