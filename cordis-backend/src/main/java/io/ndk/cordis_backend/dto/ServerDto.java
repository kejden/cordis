package io.ndk.cordis_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServerDto {
    private Long id;
    @NotBlank(message = "Server name is required!")
    @Size(min = 3, message = "Server name must have at least 3 characters!")
    @Size(max = 30, message = "Server name can have at most 30 characters!")
    private String name;
    @NotBlank(message = "Owner of server is required!")
    private UserDto owner;
    @NotBlank(message = "Image is required!")
    private String image;
}
