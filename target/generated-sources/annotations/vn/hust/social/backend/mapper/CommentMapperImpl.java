package vn.hust.social.backend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.dto.CommentDTO;
import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.comment.Comment;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-27T00:57:13+0700",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public CommentDTO toDTO(Comment comment, List<MediaDTO> medias) {
        if ( comment == null && medias == null ) {
            return null;
        }

        UserDTO user = null;
        UUID id = null;
        String content = null;
        int likesCount = 0;
        if ( comment != null ) {
            user = userMapper.toDTO( comment.getUser() );
            id = comment.getId();
            content = comment.getContent();
            if ( comment.getLikesCount() != null ) {
                likesCount = comment.getLikesCount();
            }
        }
        List<MediaDTO> medias1 = null;
        List<MediaDTO> list = medias;
        if ( list != null ) {
            medias1 = new ArrayList<MediaDTO>( list );
        }

        CommentDTO commentDTO = new CommentDTO( id, user, content, likesCount, medias1 );

        return commentDTO;
    }
}
