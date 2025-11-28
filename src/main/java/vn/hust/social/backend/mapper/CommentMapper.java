package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.comment.CommentDTO;
import vn.hust.social.backend.entity.comment.Comment;

@Mapper(componentModel = "spring", uses = {CommentMediaMapper.class})
public interface CommentMapper {

    @Mapping(target = "medias", source = "mediaList")
    CommentDTO toDTO(Comment comment);
}
