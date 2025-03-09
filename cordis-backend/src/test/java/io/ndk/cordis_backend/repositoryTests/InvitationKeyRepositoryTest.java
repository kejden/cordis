package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.InvitationKeyEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.repository.InvitationKeyRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InvitationKeyRepositoryTest {

    @Autowired
    private InvitationKeyRepository invitationKeyRepository;

    @Autowired
    private ServerRepository serverRepository;

    private ServerEntity testServer;

    @BeforeEach
    void setUp() {
        testServer = serverRepository.save(ServerEntity.builder()
                .name("Test Server")
                .build());

        invitationKeyRepository.deleteAll();
    }

    @Test
    void testFindByInvitationKey() {
        String keyValue = UUID.randomUUID().toString();
        InvitationKeyEntity entity = invitationKeyRepository.save(
                InvitationKeyEntity.builder()
                        .invitationKey(keyValue)
                        .activationTime(LocalDateTime.now())
                        .expirationTime(LocalDateTime.now().plusDays(7))
                        .server(testServer)
                        .build()
        );

        assertThat(invitationKeyRepository.findByInvitationKey(keyValue)).isPresent();
    }

    @Test
    void testFindByInvitationKeyInvalid() {
        assertThat(invitationKeyRepository.findByInvitationKey("invite-key")).isEmpty();
    }

    @Test
    void testFindByServerId() {
        InvitationKeyEntity entity = invitationKeyRepository.save(
                InvitationKeyEntity.builder()
                        .invitationKey(UUID.randomUUID().toString())
                        .activationTime(LocalDateTime.now())
                        .expirationTime(LocalDateTime.now().plusDays(7))
                        .server(testServer)
                        .build()
        );

        assertThat(invitationKeyRepository.findByServerId(testServer.getId())).hasSize(1);
    }

    @Test
    void testFindByServerIdInvalid() {
        assertThat(invitationKeyRepository.findByServerId(testServer.getId())).isEmpty();
    }

    @Test
    void testDeleteByServerId() {
        invitationKeyRepository.save(
                InvitationKeyEntity.builder()
                        .invitationKey(UUID.randomUUID().toString())
                        .activationTime(LocalDateTime.now())
                        .expirationTime(LocalDateTime.now().plusDays(7))
                        .server(testServer)
                        .build()
        );

        invitationKeyRepository.deleteByServerId(testServer.getId());
        assertThat(invitationKeyRepository.findByServerId(testServer.getId())).isEmpty();
    }
}