package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.EventParticipantDTO;
import vn.hust.social.backend.entity.event.EventParticipant;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface EventParticipantMapper {
    @Mapping(target = "user", source = "user")
    EventParticipantDTO toDTO(EventParticipant eventParticipant);
}
