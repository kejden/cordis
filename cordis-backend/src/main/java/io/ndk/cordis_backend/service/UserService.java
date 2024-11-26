package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;

import java.util.Optional;

public interface UserService {
    AccountSignUp signUp(AccountSignUp dto);
    SignInResponse signIn(SignInRequest dto);
    void logout(String email);
}
