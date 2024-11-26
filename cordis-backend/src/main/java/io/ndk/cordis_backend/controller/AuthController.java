package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final CookieService cookieService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid AccountSignUp dto) {
        return new ResponseEntity<>(userService.signUp(dto), HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@RequestBody @Valid SignInRequest dto, HttpServletResponse response) {
        response.addCookie(cookieService.getNewCookie("jwt", jwtService.generateToken(dto.getEmail())));
        return new ResponseEntity<>(userService.signIn(dto), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String jwt = cookieService.getJwtCookie(request);
        String email = jwtService.extractUserName(jwt);
        userService.logout(email);
        response.addCookie(cookieService.deleteCookie("jwt"));
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

}
