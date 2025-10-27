package vn.hust.social.backend.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter @RequiredArgsConstructor
public class ForgotPasswordRequest {
    @NonNull
    private String email;
}
