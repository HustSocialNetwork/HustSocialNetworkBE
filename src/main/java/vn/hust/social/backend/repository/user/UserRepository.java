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

    @Query("""
            select u from User u
            where (
                lower(u.displayName) like lower(concat('%', :keyword, '%'))
                or lower(u.firstName) like lower(concat('%', :keyword, '%'))
                or lower(u.lastName) like lower(concat('%', :keyword, '%'))
            )
            and u.id <> :viewerId
            and not exists (
                select 1 from Block b
                where (b.blocker.id = :viewerId and b.blocked.id = u.id)
                   or (b.blocker.id = u.id and b.blocked.id = :viewerId)
            )
            """)
    List<User> searchProfiles(String keyword, UUID viewerId);
}
