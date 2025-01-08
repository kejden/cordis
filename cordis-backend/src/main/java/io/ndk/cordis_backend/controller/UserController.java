package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.EditUserRequest;
import io.ndk.cordis_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/profile/edit")
    public ResponseEntity<UserDto> editProfile(@RequestBody @Valid EditUserRequest request, Principal principal) {
        UserDto savedUser = userService.editUser(request, principal.getName());
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @PutMapping("/profile/image")
    public ResponseEntity<String> uploadImageToFIleSystem(
            @RequestParam("image") MultipartFile file,
            Principal principal
    ){
        String imagePath = userService.updateUserImageProfile(file, principal);
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }


}
