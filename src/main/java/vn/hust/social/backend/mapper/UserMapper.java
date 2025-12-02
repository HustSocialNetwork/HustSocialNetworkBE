package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.user.ProfileDTO;
import vn.hust.social.backend.dto.user.UserDTO;
import vn.hust.social.backend.entity.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    ProfileDTO toProfileDTO(User user);
}
