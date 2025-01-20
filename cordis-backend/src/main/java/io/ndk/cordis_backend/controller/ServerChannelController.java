package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.service.ServerChannelService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/server-channel")
@AllArgsConstructor
public class ServerChannelController {

    private ServerChannelService serverChannelService;

    @PostMapping
    public ServerChannelDto createServerChannel(
            @RequestBody CreateServerChannel serverChannelDto,
            Principal principal) {
        return serverChannelService.create(serverChannelDto, principal.getName());
    }

    @PutMapping("/{id}")
    public ServerChannelDto updateServerChannel(
            @RequestParam Long id,
            @RequestBody CreateServerChannel serverChannelDto,
            Principal principal) {
        return serverChannelService.update(id, serverChannelDto, principal.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteServerChannel(
            @PathVariable Long id,
            Principal principal) {
        serverChannelService.delete(id, principal.getName());
    }
}
