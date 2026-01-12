package vn.hust.social.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.controller.PostController;
import vn.hust.social.backend.filter.JwtFilter;
import vn.hust.social.backend.service.post.PostService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import({ vn.hust.social.backend.config.SecurityConfig.class, JwtFilter.class, CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class })
public class CustomErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PostService postService;

    @Test
    public void testUnauthorizedAccess_ShouldReturn401() throws Exception {
        // No Authorization header provided
        mockMvc.perform(get("/api/posts/all")
                .param("page", "0")
                .param("pageSize", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.getCode()))
                .andExpect(jsonPath("$.message").value(ResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    public void testForbiddenAccess_ShouldReturnCustom403() throws Exception {
        // Mock valid token but invalid role
        String token = "valid_token_invalid_role";
        when(jwtUtils.extractEmail(token)).thenReturn("guest@email.com");
        when(jwtUtils.extractRole(token)).thenReturn("GUEST"); // Role not in allowed list

        mockMvc.perform(get("/api/posts/all")
                .param("page", "0")
                .param("pageSize", "10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN_ACCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResponseCode.FORBIDDEN_ACCESS.getMessage()));
    }
}
