package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;

import vn.hust.social.backend.dto.UserAuthDTO;
import vn.hust.social.backend.entity.user.UserAuth;

@Mapper(componentModel = "spring")
public interface UserAuthMapper {
    UserAuthDTO toDTO(UserAuth userAuth);
}
