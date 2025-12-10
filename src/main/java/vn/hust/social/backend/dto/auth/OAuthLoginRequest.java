package vn.hust.social.backend.dto.auth;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter @RequiredArgsConstructor
public class OAuthLoginRequest {
    @NonNull private String provider;
    @NonNull private String accessToken;
}
