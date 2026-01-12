package vn.hust.social.backend.repository.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.club.Club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.hust.social.backend.entity.enums.club.ClubFollowerStatus;
import vn.hust.social.backend.entity.enums.club.ClubModeratorStatus;

import java.util.UUID;
import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {
    boolean existsByName(String name);

    @Query("SELECT c FROM Club c JOIN ClubFollower cf ON c.id = cf.club.id WHERE cf.user.id = :userId AND cf.status = :status")
    Page<Club> findFollowedClubs(UUID userId, ClubFollowerStatus status, Pageable pageable);

    @Query("SELECT c FROM Club c JOIN ClubModerator cm ON c.id = cm.club.id WHERE cm.user.id = :userId AND cm.status = :status")
    Page<Club> findManagedClubs(UUID userId, ClubModeratorStatus status, Pageable pageable);

    @Query(value = "SELECT * FROM club WHERE MATCH(name) AGAINST(:keyword IN BOOLEAN MODE) ORDER BY created_at DESC", countQuery = "SELECT COUNT(*) FROM club WHERE MATCH(name) AGAINST(:keyword IN BOOLEAN MODE)", nativeQuery = true)
    Page<Club> searchByName(String keyword, Pageable pageable);
}
