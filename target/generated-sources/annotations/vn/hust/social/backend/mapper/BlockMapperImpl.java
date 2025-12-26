package vn.hust.social.backend.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.dto.BlockDTO;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.block.Block;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-27T00:57:13+0700",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class BlockMapperImpl implements BlockMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public BlockDTO toDTO(Block block) {
        if ( block == null ) {
            return null;
        }

        UUID id = null;
        UserDTO blocker = null;
        UserDTO blocked = null;

        id = block.getId();
        blocker = userMapper.toDTO( block.getBlocker() );
        blocked = userMapper.toDTO( block.getBlocked() );

        BlockDTO blockDTO = new BlockDTO( id, blocker, blocked );

        return blockDTO;
    }
}
