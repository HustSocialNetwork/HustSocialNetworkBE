package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class BlockController {
    private final JwtUtils jwtUtils;
    private final BlockService blockService;

    @PostMapping
    public ApiResponse<UserBlockResponse> userBlock(@RequestBody UserBlockRequest userBlockRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(blockService.userBlock(userBlockRequest.blockedUserId(), email));
    }

    @DeleteMapping("/{blockedUserId}")
    public ApiResponse<UserUnblockResponse> userUnblock(@PathVariable UUID blockedUserId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(blockService.userUnblock(blockedUserId, email));
    }

    @GetMapping
    public ApiResponse<UserGetBlocksResponse> userBlocks(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(blockService.userGetBlocks(email));
    }
}
