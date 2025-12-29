package vn.hust.social.backend.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.user.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message_read")
@Getter
@Setter
public class MessageRead extends Base {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", columnDefinition = "BINARY(16)", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", columnDefinition = "BINARY(16)", nullable = false)
    private User reader;

    @Column(name = "read_at", nullable = false)
    private Instant readAt = Instant.now();

    protected MessageRead() {
    }

    public MessageRead(Message message, User reader) {
        this.message = message;
        this.reader = reader;
    }
}
