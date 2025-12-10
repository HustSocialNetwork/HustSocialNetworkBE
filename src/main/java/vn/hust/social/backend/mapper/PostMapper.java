package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.PostDTO;
import vn.hust.social.backend.entity.post.Post;

@Mapper(componentModel = "spring", uses = {PostMediaMapper.class, CommentMapper.class, UserMapper.class})
public interface PostMapper {
    @Mapping(target = "medias", source = "mediaList")       // map Post.mediaList -> PostDTO.medias
    @Mapping(target = "user", source = "user")
    PostDTO toDTO(Post post);
}
