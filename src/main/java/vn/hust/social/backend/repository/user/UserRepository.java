package vn.hust.social.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.hust.social.backend.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByDisplayName(String displayName);

    Optional<User> getUserById(UUID id);

    @Query(value = """
            SELECT * FROM users u
            WHERE MATCH(u.display_name, u.first_name, u.last_name) AGAINST(:keyword IN BOOLEAN MODE)
            AND u.id != :viewerId
            AND NOT EXISTS (
                SELECT 1 FROM block b
                WHERE (b.blocker_id = :viewerId AND b.blocked_id = u.id)
                   OR (b.blocker_id = u.id AND b.blocked_id = :viewerId)
            )
            """, nativeQuery = true)
    List<User> searchProfiles(String keyword, UUID viewerId);
}
