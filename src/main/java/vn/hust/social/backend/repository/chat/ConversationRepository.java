package vn.hust.social.backend.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.chat.Conversation;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
