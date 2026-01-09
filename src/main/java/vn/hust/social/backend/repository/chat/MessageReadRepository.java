package vn.hust.social.backend.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.chat.MessageRead;

import java.util.UUID;

import vn.hust.social.backend.entity.chat.Message;
import vn.hust.social.backend.entity.user.User;
import java.util.Optional;

public interface MessageReadRepository extends JpaRepository<MessageRead, UUID> {
    Optional<MessageRead> findByMessageAndReader(Message message, User reader);
}
