package vn.hust.social.backend.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.chat.Conversation;
import vn.hust.social.backend.entity.chat.ConversationMember;
import vn.hust.social.backend.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {
    List<ConversationMember> findByMember(User member);

    List<ConversationMember> findByConversation(Conversation conversation);

    Optional<ConversationMember> findByConversationAndMemberId(Conversation conversation, UUID memberId);

    void deleteByConversation(Conversation conversation);
}
