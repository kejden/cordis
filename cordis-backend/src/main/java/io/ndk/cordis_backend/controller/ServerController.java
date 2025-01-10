package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import io.ndk.cordis_backend.service.ServerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/server")
@AllArgsConstructor
public class ServerController {

    private final ServerService serverService;

    public ResponseEntity<ServerDto> getServerById(@PathVariable Long id) {
        ServerDto serverDto = serverService.getServerById(id);
        return ResponseEntity.ok(serverDto);
    }

    @PostMapping
    public ResponseEntity<ServerDto> createServer(@RequestBody ServerCreate dto, Principal principal) {
        ServerDto createdServer = serverService.createServer(dto, principal.getName());
        return new ResponseEntity<>(createdServer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDto> updateServer(@PathVariable Long id, @RequestBody ServerDto dto) {
        ServerDto updatedServer = serverService.updateServer(id, dto);
        return new ResponseEntity<>(updatedServer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        serverService.deleteServer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<String> uploadImageToFIleSystem(
            @RequestParam("image") MultipartFile file,
            @PathVariable Long id
    ){
        String imagePath = serverService.updateServerImage(file, id);
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }


}
