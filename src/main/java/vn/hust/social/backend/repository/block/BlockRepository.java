package vn.hust.social.backend.repository.block;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<Block, UUID> {
    Optional<Block> findBlockByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);
    List<Block> findBlocksByBlockerId(UUID blockerId);

    boolean existsByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);
}
