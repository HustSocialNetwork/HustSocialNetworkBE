package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.ConversationMemberDTO;
import vn.hust.social.backend.entity.chat.ConversationMember;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConversationMemberMapper {
    @Mapping(source = "role", target = "memberType")
    ConversationMemberDTO toDTO(ConversationMember conversationMember);
}
