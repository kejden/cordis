package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInRequest {
    @Email(message = "Email is not in a valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @Length(min=8, max= 15)
    @NotBlank(message = "Password is required!")
    private String password;
}
