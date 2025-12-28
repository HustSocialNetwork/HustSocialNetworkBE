package vn.hust.social.backend.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.enums.chat.MemberType;
import vn.hust.social.backend.entity.user.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation_member",
        indexes = {
                        @Index(name = "idx_conversation_member_conversation_id", columnList = "conversation_id"),
                        @Index(name = "idx_conversation_member_member_id", columnList = "member_id")
        })
@Getter
@Setter
public class ConversationMember extends Base {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", columnDefinition = "BINARY(16)", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)", nullable = false)
    private User member;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MemberType role = MemberType.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    protected ConversationMember() {}

    public ConversationMember(Conversation conversation, User member, MemberType role) {
        this.conversation = conversation;
        this.member = member;
        this.role = role;
    }
}
