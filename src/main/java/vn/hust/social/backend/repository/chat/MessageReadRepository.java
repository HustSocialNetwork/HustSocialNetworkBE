package vn.hust.social.backend.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.chat.MessageRead;

import java.util.UUID;

public interface MessageReadRepository extends JpaRepository<MessageRead, UUID> {
}
