package vn.hust.social.backend.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.enums.chat.ConversationType;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

@Entity
@Table(name = "conversation",
        indexes = {
                        @Index(name = "idx_title", columnList = "title, type")
        })
@Getter
@Setter
public class Conversation extends Base {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ConversationType type = ConversationType.PRIVATE;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    private User createdBy;

    protected Conversation() {}

    public Conversation(ConversationType type, String title, User createdBy) {
        this.type = type;
        this.title = title;
        this.createdBy = createdBy;
    }
}
