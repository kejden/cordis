package io.ndk.cordis_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {
    @NotBlank(message = "Username is required!")
    @Size(min = 3, message = "Username must have at least 3 characters!")
    @Size(max = 20, message = "Username can have at most 20 characters!")
    private String userName;
}
