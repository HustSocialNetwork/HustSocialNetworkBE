package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.block.block.UserBlockRequest;
import vn.hust.social.backend.dto.block.block.UserBlockResponse;
import vn.hust.social.backend.dto.block.get.UserGetBlocksResponse;
import vn.hust.social.backend.dto.block.unblock.UserUnblockResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.block.BlockService;

import java.util.UUID;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@Tag(name = "Block", description = "User Blocking APIs")
@PreAuthorize("hasRole('USER')")
public class BlockController {
    private final JwtUtils jwtUtils;
    private final BlockService blockService;

    @PostMapping
    @Operation(summary = "Block user", description = "Block another user by ID")
    public ApiResponse<UserBlockResponse> userBlock(@RequestBody UserBlockRequest userBlockRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(blockService.userBlock(userBlockRequest.blockedUserId(), email));
    }

    @DeleteMapping("/{blockedUserId}")
    @Operation(summary = "Unblock user", description = "Unblock a user by ID")
    public ApiResponse<UserUnblockResponse> userUnblock(@PathVariable UUID blockedUserId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(blockService.userUnblock(blockedUserId, email));
    }

    @GetMapping
    @Operation(summary = "Get blocked users", description = "Get list of users blocked by the current user")
    public ApiResponse<UserGetBlocksResponse> userBlocks(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(blockService.userGetBlocks(email));
    }
}
