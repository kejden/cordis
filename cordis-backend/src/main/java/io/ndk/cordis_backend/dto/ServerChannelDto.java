package io.ndk.cordis_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ServerChannelDto {
    private Long id;
//    private ServerDto server;
    @NotBlank(message = "Server channel name is required!")
    @Size(min = 3, message = "Server channel name must have at least 3 characters!")
    @Size(max = 30, message = "Server channel name can have at most 30 characters!")
    private String name;
}
