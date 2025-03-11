package io.ndk.cordis_backend.dto.request;


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
public class CreateMemberRoles {
    @NotBlank(message = "ServerID is required!")
    private Long serverId;
    @NotBlank(message = "MemberID is required!")
    private Long memberId;
    @NotBlank(message = "Role is required!")
    @Size(min = 3, message = "Role name must have at least 3 characters!")
    @Size(max = 30, message = "Role name can have at most 30 characters!")
    private String role;
}
