package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditUserRequest {
    @NotBlank(message = "Username is required!")
    @Size(min = 3, message = "Username must have at least 3 characters!")
    @Size(max = 20, message = "Username can have at most 20 characters!")
    private String username;
}
