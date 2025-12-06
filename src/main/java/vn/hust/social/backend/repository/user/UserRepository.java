package vn.hust.social.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    LocalDateTime findByCreatedAt(LocalDateTime createdAt);
    String findByDisplayName(String displayName);
    boolean existsByDisplayName(String displayName);

    Optional<User> getUserById(UUID id);
}
