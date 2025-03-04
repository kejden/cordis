package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInRequest {
    @Email(message = "Email is not in a valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @Size(min = 8, message = "Password must have at least 8 characters!")
    @Size(max = 15, message = "Password can have at most 15 characters!")
    @NotBlank(message = "Password is required!")
    private String password;
}
