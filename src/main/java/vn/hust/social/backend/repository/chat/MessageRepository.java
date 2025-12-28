package vn.hust.social.backend.repository.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hust.social.backend.entity.chat.Conversation;
import vn.hust.social.backend.entity.chat.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Optional<Message> findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.createdAt > :after ORDER BY m.createdAt DESC")
    List<Message> findByConversationAfterTimestamp(@Param("conversation") Conversation conversation, @Param("after") Instant after, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.createdAt DESC")
    List<Message> findByConversationOrderByCreatedAtDesc(@Param("conversation") Conversation conversation, Pageable pageable);
}
