package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.user.UserAuth.AuthProvider;

public record UserAuthDTO(
                String email,
                AuthProvider provider) {
}
