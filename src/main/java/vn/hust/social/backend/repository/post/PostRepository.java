package vn.hust.social.backend.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hust.social.backend.entity.post.Post;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findByPostId(UUID postId);
    @Query("""
    SELECT p FROM Post p
    WHERE p.user.id = :ownerId
      AND (
            p.visibility = 'PUBLIC'
            OR (
                p.visibility = 'FRIENDS'
                AND EXISTS (
                    SELECT 1 FROM Friendship f
                    WHERE
                        (
                          (f.requester.id = :viewerId AND f.receiver.id = :ownerId)
                          OR
                          (f.requester.id = :ownerId AND f.receiver.id = :viewerId)
                        )
                        AND f.status = 'ACCEPTED'
                )
            )
            OR (
                p.visibility = 'PRIVATE'
                AND :viewerId = :ownerId
            )
          )
      AND NOT EXISTS (
          SELECT 1 FROM Friendship f
          WHERE
              (
                (f.requester.id = :viewerId AND f.receiver.id = :ownerId)
                OR
                (f.requester.id = :ownerId AND f.receiver.id = :viewerId)
              )
              AND f.status = 'BLOCKED'
      )
    """)
    Page<Post> findPostsByUser(
            @Param("ownerId") UUID ownerId,
            @Param("viewerId") UUID viewerId,
            Pageable pageable
    );
    @Query("""
    SELECT p FROM Post p
    WHERE
        (
            p.user.id = :viewerId
            OR EXISTS (
                SELECT 1 FROM Friendship f
                WHERE
                    (
                        (f.requester.id = :viewerId AND f.receiver.id = p.user.id)
                        OR
                        (f.requester.id = p.user.id AND f.receiver.id = :viewerId)
                    )
                    AND f.status = 'ACCEPTED'
            )
        )
        AND (
            p.visibility = 'PUBLIC'
            OR (
                p.visibility = 'FRIENDS'
                AND EXISTS (
                    SELECT 1 FROM Friendship f2
                    WHERE
                        (
                            (f2.requester.id = :viewerId AND f2.receiver.id = p.user.id)
                            OR
                            (f2.requester.id = p.user.id AND f2.receiver.id = :viewerId)
                        )
                        AND f2.status = 'ACCEPTED'
                )
            )
            OR (
                p.visibility = 'PRIVATE'
                AND p.user.id = :viewerId
            )
        )
        AND NOT EXISTS (
            SELECT 1 FROM Friendship f3
            WHERE
                (
                    (f3.requester.id = :viewerId AND f3.receiver.id = p.user.id)
                    OR
                    (f3.requester.id = p.user.id AND f3.receiver.id = :viewerId)
                )
                AND f3.status = 'BLOCKED'
        )
    """)
    Page<Post> findPostsOfFriends(
            @Param("viewerId") UUID viewerId,
            Pageable pageable
    );
    @Query("""
    SELECT p FROM Post p
    WHERE
        p.visibility = 'PUBLIC'
        OR (
            p.visibility = 'FRIENDS'
            AND EXISTS (
                SELECT 1 FROM Friendship f
                WHERE
                    (
                        (f.requester.id = :viewerId AND f.receiver.id = p.user.id)
                        OR
                        (f.requester.id = p.user.id AND f.receiver.id = :viewerId)
                    )
                    AND f.status = 'ACCEPTED'
            )
        )
        OR (
            p.visibility = 'PRIVATE'
            AND p.user.id = :viewerId
        )
        AND NOT EXISTS (
            SELECT 1 FROM Friendship f2
            WHERE
                (
                    (f2.requester.id = :viewerId AND f2.receiver.id = p.user.id)
                    OR
                    (f2.requester.id = p.user.id AND f2.receiver.id = :viewerId)
                )
                AND f2.status = 'BLOCKED'
        )
    """)
    Page<Post> findAllVisibleToViewer(
            @Param("viewerId") UUID viewerId,
            Pageable pageable
    );

}
