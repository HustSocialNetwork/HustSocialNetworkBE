package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.ConversationDTO;
import vn.hust.social.backend.dto.ConversationMemberDTO;
import vn.hust.social.backend.dto.MessageDTO;

import java.util.List;

public record GetConversationResponse(
                ConversationDTO conversation,
                List<ConversationMemberDTO> participants,
                MessageDTO lastMessage) {
}
