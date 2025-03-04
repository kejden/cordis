package io.ndk.cordis_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSignUp {

    @Email(message = "Email is not in a valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @JsonProperty(access = Access.WRITE_ONLY)
    @Size(min = 8, message = "Password must have at least 8 characters!")
    @Size(max = 15, message = "Password can have at most 15 characters!")
    @NotBlank(message = "Password is required!")
    private String password;

    @Size(min = 3, message = "Username must have at least 3 characters!")
    @Size(max = 20, message = "Username can have at most 20 characters!")
    @NotBlank(message = "Username is required!")
    private String userName;
}
