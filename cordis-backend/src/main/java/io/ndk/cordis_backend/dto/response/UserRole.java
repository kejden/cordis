package io.ndk.cordis_backend.dto.response;

import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {
    private UserDto user;
    private RoleEntity role;
}
