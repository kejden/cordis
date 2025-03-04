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
public class RoleDto {
    private Long id;
    @NotBlank(message = "Role name is required!")

    private String name;
}
