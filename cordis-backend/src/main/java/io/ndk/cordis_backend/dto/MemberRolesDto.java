package io.ndk.cordis_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberRolesDto {
//    private Long id;
    @NotBlank(message = "Server is required!")
    private ServerDto server;
    @NotBlank(message = "User is required!")
    private UserDto user;
    @NotBlank(message = "Role is required!")
    private RoleDto role;
}
