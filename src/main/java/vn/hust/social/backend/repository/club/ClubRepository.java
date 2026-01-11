package vn.hust.social.backend.repository.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.club.Club;

import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {
    boolean existsByName(String name);
}
