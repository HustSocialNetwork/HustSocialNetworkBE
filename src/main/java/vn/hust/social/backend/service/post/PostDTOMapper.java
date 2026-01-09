package vn.hust.social.backend.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.dto.PostDTO;
import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.mapper.MediaMapper;
import vn.hust.social.backend.mapper.UserMapper;
import vn.hust.social.backend.repository.media.MediaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostDTOMapper {

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final UserMapper userMapper;

    public PostDTO toDTO(Post post) {
        List<MediaDTO> medias = mediaRepository
                .findByTargetIdAndTargetType(post.getPostId(), MediaTargetType.POST)
                .stream()
                .map(mediaMapper::toDTO)
                .toList();

        User user = post.getUser();

        return new PostDTO(
                post.getPostId(),
                userMapper.toDTO(user),
                post.getContent(),
                post.getStatus(),
                post.getVisibility(),
                post.getLikesCount(),
                post.getCommentsCount(),
                medias,
                post.getCreatedAt());
    }
}
