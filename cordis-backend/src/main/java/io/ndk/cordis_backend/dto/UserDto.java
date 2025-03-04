package io.ndk.cordis_backend.dto;

import io.ndk.cordis_backend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {
    private Long id;
    @Email(message = "Email is not in a valid format!")
    @NotBlank(message = "Email is required!")
    private String email;
    @NotBlank(message = "Username is required!")
    @Size(min = 3, message = "Username must have at least 3 characters!")
    @Size(max = 20, message = "Username can have at most 20 characters!")
    private String userName;
    private UserStatus status;
    @NotBlank(message = "Profile image is required!")
    private String profileImage;
}
