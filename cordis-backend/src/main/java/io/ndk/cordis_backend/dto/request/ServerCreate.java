package io.ndk.cordis_backend.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServerCreate {
    @NotBlank(message = "Server name is required!")
    private String name;
    @NotBlank(message = "Server image is required!")
    private String image;
}
