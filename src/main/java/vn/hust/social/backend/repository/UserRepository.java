package vn.hust.social.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    LocalDateTime findByCreatedAt(LocalDateTime createdAt);

    String findByDisplayName(String displayName);

    boolean existsByDisplayName(String displayName);
}
