package vn.hust.social.backend.repository.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.club.ClubFollower;
import vn.hust.social.backend.entity.enums.club.ClubFollowerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

@Repository
public interface ClubFollowerRepository extends JpaRepository<ClubFollower, UUID> {
    List<ClubFollower> findAllByUserIdAndClubIdInAndStatus(UUID userId, List<UUID> clubIds, ClubFollowerStatus status);

    boolean existsByClubIdAndUserId(UUID clubId, UUID userId);

    Optional<ClubFollower> findByClubIdAndUserId(UUID clubId, UUID userId);

    long countByClubId(UUID clubId);

    Page<ClubFollower> findByUserIdAndStatus(UUID userId, ClubFollowerStatus status, Pageable pageable);
}
