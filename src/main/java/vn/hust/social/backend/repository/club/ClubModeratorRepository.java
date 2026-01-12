package vn.hust.social.backend.repository.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.club.ClubModerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.hust.social.backend.entity.enums.club.ClubModeratorStatus;
import vn.hust.social.backend.entity.enums.club.ClubRole;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ClubModeratorRepository extends JpaRepository<ClubModerator, UUID> {
    Optional<ClubModerator> findByClubIdAndUserId(UUID clubId, UUID userId);

    List<ClubModerator> findAllByClubIdAndRoleAndStatus(UUID clubId, ClubRole role, ClubModeratorStatus status);

    Page<ClubModerator> findByUserIdAndStatus(UUID userId, ClubModeratorStatus status, Pageable pageable);
}
