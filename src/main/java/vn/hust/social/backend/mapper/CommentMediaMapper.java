package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.media.CommentMediaDTO;
import vn.hust.social.backend.entity.comment.CommentMedia;

@Mapper(componentModel = "spring")
public interface CommentMediaMapper {
    CommentMediaDTO toDTO(CommentMedia media);
}
