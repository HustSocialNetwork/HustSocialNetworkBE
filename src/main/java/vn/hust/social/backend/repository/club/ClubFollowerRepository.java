package vn.hust.social.backend.repository.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.club.ClubFollower;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubFollowerRepository extends JpaRepository<ClubFollower, UUID> {
    boolean existsByClubIdAndUserId(UUID clubId, UUID userId);

    Optional<ClubFollower> findByClubIdAndUserId(UUID clubId, UUID userId);

    long countByClubId(UUID clubId);
}
