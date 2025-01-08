package io.ndk.cordis_backend.dto.response;

import io.ndk.cordis_backend.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendResponse {
    private Long id;
    private UserDto user;
    private String state;

}
