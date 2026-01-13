package vn.hust.social.backend.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.event.EventParticipant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    Optional<EventParticipant> findByEventIdAndUserId(UUID eventId, UUID userId);

    List<EventParticipant> findByUserIdAndEventIdIn(UUID userId, List<UUID> eventIds);

    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);

    Page<EventParticipant> findByEventId(UUID eventId, Pageable pageable);

    @Query(value = """
            SELECT ep.* FROM event_participant ep
            JOIN users u ON ep.user_id = u.id
            WHERE ep.event_id = :eventId
            AND MATCH(u.display_name, u.first_name, u.last_name) AGAINST(:keyword IN BOOLEAN MODE)
            ORDER BY ep.registered_at DESC
            """, countQuery = """
            SELECT COUNT(*) FROM event_participant ep
            JOIN users u ON ep.user_id = u.id
            WHERE ep.event_id = :eventId
            AND MATCH(u.display_name, u.first_name, u.last_name) AGAINST(:keyword IN BOOLEAN MODE)
            """, nativeQuery = true)
    Page<EventParticipant> searchByEventIdAndKeyword(UUID eventId, String keyword, Pageable pageable);
}
