package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;

import java.util.Optional;

public interface UserService {
    Optional<AccountSignUp> signUp(AccountSignUp dto);

    Optional<SignInResponse> signIn(SignInRequest dto);
}
