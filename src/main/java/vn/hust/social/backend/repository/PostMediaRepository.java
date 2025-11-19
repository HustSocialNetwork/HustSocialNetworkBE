package vn.hust.social.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.post.PostMedia;

import java.util.List;
import java.util.UUID;

public interface PostMediaRepository extends JpaRepository<PostMedia, String> {
        List<PostMedia> findByPost_PostId(UUID postPostId);
}
