package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.MessageDTO;
import vn.hust.social.backend.entity.chat.Message;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {
    MessageDTO toDTO(Message message);
}
