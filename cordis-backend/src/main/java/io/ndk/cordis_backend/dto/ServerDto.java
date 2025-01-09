package io.ndk.cordis_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServerDto {
    private Long id;
    private String name;
    private UserDto owner;
    private String image;
}
