package vn.hust.social.backend.dto.chat;

import java.util.List;
import java.util.UUID;

public record AddMemberRequest(
        List<UUID> userIds) {
}
