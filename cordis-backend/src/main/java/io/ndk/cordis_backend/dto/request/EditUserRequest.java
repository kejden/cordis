package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditUserRequest {
    @NotBlank(message = "Username is required!")
    private String username;
}
