package io.ndk.cordis_backend.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateMemberRoles {
    @NotBlank(message = "ServerID is required!")
    private Long serverId;
    @NotBlank(message = "MemberID is required!")
    private Long memberId;
    @NotBlank(message = "Role is required!")
    private String role;
}
