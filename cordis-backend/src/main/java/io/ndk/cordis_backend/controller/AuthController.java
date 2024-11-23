package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid AccountSignUp dto) {
        return new ResponseEntity<>(userService.signUp(dto), HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@RequestBody @Valid SignInRequest dto) {
        return new ResponseEntity<>(userService.signIn(dto), HttpStatus.OK);
    }

}
