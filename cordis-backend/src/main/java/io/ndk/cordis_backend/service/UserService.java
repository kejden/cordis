package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.EditUserRequest;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface UserService {
    AccountSignUp signUp(AccountSignUp dto);
    SignInResponse signIn(SignInRequest dto);
    void logout(String email);
    UserDto updateUser(String email, UserDto dto);
    UserDto editUser(EditUserRequest request, String email);
    String updateUserImageProfile(MultipartFile file, Principal principal);
}
