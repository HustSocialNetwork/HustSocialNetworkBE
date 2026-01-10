package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.EventDTO;
import vn.hust.social.backend.entity.event.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "clubId", source = "club.id")
    @Mapping(target = "clubName", source = "club.name")
    @Mapping(target = "myRegistrationStatus", ignore = true)
    EventDTO toDTO(Event event);
}
