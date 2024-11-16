package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;
import io.ndk.cordis_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid AccountSignUp dto) {
        Optional<AccountSignUp> potentialUser = userService.signUp(dto);
        if(potentialUser.isPresent()) {
            return new ResponseEntity<>(potentialUser.get(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Email is already in use.", HttpStatus.CONFLICT);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@RequestBody @Valid SignInRequest dto) {
        Optional<SignInResponse> response = userService.signIn(dto);
        if(response.isPresent()) {
            return new ResponseEntity<>(response.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid credentials.", HttpStatus.CONFLICT);
    }

}
