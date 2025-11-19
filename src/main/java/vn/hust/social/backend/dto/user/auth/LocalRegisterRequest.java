package vn.hust.social.backend.dto.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vn.hust.social.backend.validation.ValidPassword;

@Getter @Setter @RequiredArgsConstructor
public class LocalRegisterRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String displayName;
    @NotBlank @Email(message = "Invalid email.")
    private String email;
    @NotBlank @ValidPassword
    private String password;
}
