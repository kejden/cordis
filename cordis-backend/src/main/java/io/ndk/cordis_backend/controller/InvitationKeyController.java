package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.entity.InvitationKeyEntity;
import io.ndk.cordis_backend.service.InvitationKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitation-keys")
@RequiredArgsConstructor
public class InvitationKeyController {
    private final InvitationKeyService invitationKeyService;

    @PostMapping("/generate/{serverId}")
    public ResponseEntity<InvitationKeyEntity> generateInvitationKey(@PathVariable Long serverId) {
        InvitationKeyEntity key = invitationKeyService.generateInvitationKey(serverId);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/active/{serverId}")
    public ResponseEntity<List<InvitationKeyEntity>> getActiveInvitationKeys(@PathVariable Long serverId) {
        List<InvitationKeyEntity> keys = invitationKeyService.getActiveInvitationKeys(serverId);
        return ResponseEntity.ok(keys);
    }
}
