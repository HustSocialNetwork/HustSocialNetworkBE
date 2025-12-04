package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.PostMediaDTO;
import vn.hust.social.backend.entity.post.PostMedia;

@Mapper(componentModel = "spring")
public interface PostMediaMapper {
    PostMediaDTO toDTO(PostMedia media);
}
