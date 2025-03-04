package io.ndk.cordis_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSignUp {

    @Email(message = "Email is not in a valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @Length(min=8, max = 15)
    @JsonProperty(access = Access.WRITE_ONLY)
    @NotBlank(message = "Password is required!")
    private String password;

    @Length(min=2, max = 15)
    @NotBlank(message = "Username is required!")
    private String userName;
}
