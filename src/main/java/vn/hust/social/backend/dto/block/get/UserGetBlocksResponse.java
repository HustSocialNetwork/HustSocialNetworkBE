package vn.hust.social.backend.dto.block.get;

import vn.hust.social.backend.dto.BlockDTO;

import java.util.List;

public record UserGetBlocksResponse(
        List<BlockDTO> blockList
) {
}
