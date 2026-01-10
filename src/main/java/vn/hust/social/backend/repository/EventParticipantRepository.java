package vn.hust.social.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.event.EventParticipant;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    Optional<EventParticipant> findByEventIdAndUserId(UUID eventId, UUID userId);

    List<EventParticipant> findByUserIdAndEventIdIn(UUID userId, List<UUID> eventIds);
}
