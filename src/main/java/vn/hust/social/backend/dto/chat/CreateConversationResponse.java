package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.ConversationDTO;
import vn.hust.social.backend.dto.ConversationMemberDTO;

import java.util.List;

public record CreateConversationResponse(
        ConversationDTO conversation,
        List<ConversationMemberDTO> members) {
}
