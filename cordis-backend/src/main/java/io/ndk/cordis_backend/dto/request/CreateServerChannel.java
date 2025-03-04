package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateServerChannel {
    @NotBlank(message = "Channel name is required!")
    private String name;
    @NotBlank(message = "ServerID is required!")
    private Long serverId;
}
