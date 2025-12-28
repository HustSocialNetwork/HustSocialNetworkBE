package vn.hust.social.backend.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.enums.chat.MessageType;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

@Entity
@Table(name = "message",
        indexes = {
                   @Index(name = "idx_message_conversation_id", columnList = "conversation_id"),
                   @Index(name = "idx_message_content", columnList = "content")
        })
@Getter
@Setter
public class Message extends Base {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", columnDefinition = "BINARY(16)", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", columnDefinition = "BINARY(16)", nullable = false)
    private User sender;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private MessageType type;

    protected Message() {}

    public Message(Conversation conversation, User sender, String content, MessageType type) {
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.type = type;
    }
}
