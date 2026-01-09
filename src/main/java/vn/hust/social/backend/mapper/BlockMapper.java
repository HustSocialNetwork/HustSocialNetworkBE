package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import vn.hust.social.backend.dto.BlockDTO;
import vn.hust.social.backend.entity.block.Block;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BlockMapper {
    BlockDTO toDTO(Block block);
}
