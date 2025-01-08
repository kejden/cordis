package io.ndk.cordis_backend.dto;

import io.ndk.cordis_backend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String userName;
    private UserStatus status;
    private String profileImage;
}
