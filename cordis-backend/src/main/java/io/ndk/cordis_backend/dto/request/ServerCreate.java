package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerCreate {
    @NotBlank(message = "Server name is required!")
    @Size(min = 3, message = "Server name must have at least 3 characters!")
    @Size(max = 30, message = "Server name can have at most 30 characters!")
    private String name;
    @NotBlank(message = "Server image is required!")
    private String image;
}
