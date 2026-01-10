package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hust.social.backend.common.response.ApiResponse;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health Check APIs")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check health", description = "Check if the application is running")
    public ApiResponse<String> checkHealth() {
        return ApiResponse.success("OK");
    }
}
