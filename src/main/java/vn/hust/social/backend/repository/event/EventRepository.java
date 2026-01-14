package vn.hust.social.backend.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.hust.social.backend.entity.event.Event;

import java.util.UUID;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query(value = "SELECT * FROM event WHERE MATCH(title) AGAINST(:keyword IN BOOLEAN MODE) ORDER BY start_time DESC", countQuery = "SELECT COUNT(*) FROM event WHERE MATCH(title) AGAINST(:keyword IN BOOLEAN MODE)", nativeQuery = true)
    Page<Event> searchByTitle(String keyword, Pageable pageable);

    List<Event> findByClubId(UUID clubId);

    void deleteByClubId(UUID clubId);
}
