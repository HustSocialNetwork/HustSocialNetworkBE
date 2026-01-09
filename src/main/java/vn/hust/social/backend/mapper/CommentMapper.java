package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.CommentDTO;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.entity.comment.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface CommentMapper {

    @Mapping(target = "medias", source = "medias")
    @Mapping(target = "user", source = "comment.user")
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "likesCount", source = "comment.likesCount")
    CommentDTO toDTO(Comment comment, List<MediaDTO> medias);
}
