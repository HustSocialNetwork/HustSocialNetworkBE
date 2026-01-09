package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.dto.PostDTO;
import vn.hust.social.backend.entity.post.Post;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class, MediaMapper.class })
public interface PostMapper {

    @Mapping(target = "medias", source = "medias")
    @Mapping(target = "user", source = "post.user")
    @Mapping(target = "postId", source = "post.postId")
    @Mapping(target = "content", source = "post.content")
    @Mapping(target = "status", source = "post.status")
    @Mapping(target = "visibility", source = "post.visibility")
    @Mapping(target = "likesCount", source = "post.likesCount")
    @Mapping(target = "commentsCount", source = "post.commentsCount")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "likedByViewer", source = "likedByViewer")
    PostDTO toDTO(Post post, List<MediaDTO> medias, boolean likedByViewer);

}
