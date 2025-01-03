package io.ndk.cordis_backend.dto;

import io.ndk.cordis_backend.enums.UserStatus;

public class UserDto {
    private Long id;
    private String email;
    private String userName;
    private UserStatus status;
}
