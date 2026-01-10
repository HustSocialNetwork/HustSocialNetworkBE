package vn.hust.social.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.event.Event;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

}
