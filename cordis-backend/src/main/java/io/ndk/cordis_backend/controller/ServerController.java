package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import io.ndk.cordis_backend.dto.response.ServerResponse;
import io.ndk.cordis_backend.dto.response.UserRole;
import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.service.ServerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/server")
@AllArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @GetMapping("/{id}")
    public ResponseEntity<ServerDto> getServerById(
            @PathVariable Long id) {
        return new ResponseEntity<>(serverService.getServerById(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<RoleEntity> getUserRoles(
            @PathVariable Long id,
            Principal principal
    ){
        return new ResponseEntity<>(serverService.getUsersRoleForServer(id, principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserRole>> getServerUsers(
            @PathVariable Long id) {
        return new ResponseEntity<>(serverService.getUsersOfServer(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllServersOfUSer(
            Principal principal) {
        return new ResponseEntity<>(serverService.getAllServerOfUser(principal.getName()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ServerDto> createServer(
            @RequestBody ServerCreate dto,
            Principal principal) {
        return new ResponseEntity<>(serverService.createServer(dto, principal.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDto> updateServer(
            @PathVariable Long id,
            @RequestBody ServerDto dto,
            Principal principal) {
        return new ResponseEntity<>(serverService.updateServer(id, dto, principal.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(
            @PathVariable Long id,
            Principal principal) {
        serverService.deleteServer(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<String> uploadImageToFIleSystem(
            @RequestParam("image") MultipartFile file,
            @PathVariable Long id
            , Principal principal
    ){
        String imagePath = serverService.updateServerImage(file, id, principal.getName());
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }
}