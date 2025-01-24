package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.entity.InvitationKeyEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.repository.InvitationKeyRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.service.InvitationKeyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvitationKeyServiceImpl implements InvitationKeyService {
    private final InvitationKeyRepository invitationKeyRepository;
    private final ServerRepository serverRepository;

    @Override
    public InvitationKeyEntity generateInvitationKey(Long serverId) {
        ServerEntity server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        InvitationKeyEntity invitationKey = InvitationKeyEntity.builder()
                .invitationKey(UUID.randomUUID().toString())
                .activationTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusWeeks(1)) // Klucz ważny przez tydzień
                .server(server)
                .build();

        return invitationKeyRepository.save(invitationKey);
    }

    @Override
    public List<InvitationKeyEntity> getActiveInvitationKeys(Long serverId) {
        return invitationKeyRepository.findByServerId(serverId);
    }

    @Override
    public boolean validateInvitationKey(String invitationKey) {
        return invitationKeyRepository.findByInvitationKey(invitationKey)
                .map(key -> key.getExpirationTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    public Long getServerIdByInvitationKey(String invitationKey) {
        return invitationKeyRepository.findByInvitationKey(invitationKey)
                .map(key -> key.getServer().getId())
                .orElseThrow(() -> new RuntimeException("Invitation key not found"));
    }
}
