package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.service.ServerChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/server-channel")
@AllArgsConstructor
public class ServerChannelController {

    private ServerChannelService serverChannelService;

    @GetMapping("/{id}")
    public ResponseEntity<List<ServerChannelDto>> getServerChannel(
            @PathVariable Long id) {
        return new ResponseEntity<>(serverChannelService.getByServerId(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ServerChannelDto> createServerChannel(
            @RequestBody CreateServerChannel serverChannelDto,
            Principal principal) {
        ServerChannelDto createdChannel = serverChannelService.create(serverChannelDto, principal.getName());
        return new ResponseEntity<>(createdChannel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerChannelDto> updateServerChannel(
            @PathVariable Long id,
            @RequestBody CreateServerChannel serverChannelDto,
            Principal principal) {
        ServerChannelDto updatedChannel = serverChannelService.update(id, serverChannelDto, principal.getName());
        return new ResponseEntity<>(updatedChannel, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServerChannel(
            @PathVariable Long id,
            Principal principal) {
        serverChannelService.delete(id, principal.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
