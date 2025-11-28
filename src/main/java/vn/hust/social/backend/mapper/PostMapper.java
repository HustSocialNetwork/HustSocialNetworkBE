package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.hust.social.backend.dto.post.PostDTO;
import vn.hust.social.backend.entity.post.Post;

@Mapper(componentModel = "spring", uses = {PostMediaMapper.class, CommentMapper.class})
public interface PostMapper {

    @Mapping(target = "medias", source = "mediaList")       // map Post.mediaList -> PostDTO.medias
    @Mapping(target = "comments", source = "comments")     // map Post.comments -> PostDTO.comments
    PostDTO toDTO(Post post);
}
