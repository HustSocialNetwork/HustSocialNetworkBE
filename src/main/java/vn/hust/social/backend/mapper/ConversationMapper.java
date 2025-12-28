package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.ConversationDTO;
import vn.hust.social.backend.entity.chat.Conversation;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConversationMapper {
    ConversationDTO toDTO(Conversation conversation);
}
