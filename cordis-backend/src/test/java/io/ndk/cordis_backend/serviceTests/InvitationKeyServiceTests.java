package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.entity.InvitationKeyEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.repository.InvitationKeyRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.service.impl.InvitationKeyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class InvitationKeyServiceTests {

    @Mock
    private ServerRepository serverRepository;
    @Mock
    private InvitationKeyRepository invitationKeyRepository;

    @InjectMocks
    private InvitationKeyServiceImpl invitationKeyService;

    private ServerEntity testServer;
    private InvitationKeyEntity testKey;

    @BeforeEach
    void setUp() {
        testServer = ServerEntity.builder()
                .id(1L)
                .name("Test Server")
                .build();

        testKey = InvitationKeyEntity.builder()
                .id(100L)
                .invitationKey("test-invite")
                .activationTime(LocalDateTime.now().minusDays(1))
                .expirationTime(LocalDateTime.now().plusDays(1))
                .server(testServer)
                .build();
    }

    @Test
    void generateInvitationKey_shouldReturnKey_whenServerExists() {
        when(serverRepository.findById(1L)).thenReturn(Optional.of(testServer));
        when(invitationKeyRepository.save(any(InvitationKeyEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        InvitationKeyEntity result = invitationKeyService.generateInvitationKey(1L);

        assertNotNull(result);
        assertEquals(testServer, result.getServer());
        assertNotNull(result.getInvitationKey());
        verify(invitationKeyRepository).save(any(InvitationKeyEntity.class));
    }

    @Test
    void generateInvitationKey_shouldThrowException_whenServerNotFound() {
        when(serverRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> invitationKeyService.generateInvitationKey(999L)
        );
        assertTrue(ex.getMessage().contains("Server not found"));
        verify(serverRepository).findById(999L);
        verify(invitationKeyRepository, never()).save(any(InvitationKeyEntity.class));
    }

    @Test
    void getActiveInvitationKeys_shouldReturnKeys() {
        when(invitationKeyRepository.findByServerId(1L)).thenReturn(List.of(testKey));

        List<InvitationKeyEntity> result = invitationKeyService.getActiveInvitationKeys(1L);

        assertEquals(1, result.size());
        assertEquals("test-invite", result.get(0).getInvitationKey());
        verify(invitationKeyRepository).findByServerId(1L);
    }

    @Test
    void getActiveInvitationKeys_shouldReturnEmpty_whenNoKeys() {
        when(invitationKeyRepository.findByServerId(2L)).thenReturn(Collections.emptyList());

        List<InvitationKeyEntity> result = invitationKeyService.getActiveInvitationKeys(2L);

        assertTrue(result.isEmpty());
        verify(invitationKeyRepository).findByServerId(2L);
    }

    @Test
    void validateInvitationKey_shouldReturnTrue_whenKeyIsValid() {
        when(invitationKeyRepository.findByInvitationKey("validKey"))
                .thenReturn(Optional.of(testKey));

        boolean result = invitationKeyService.validateInvitationKey("validKey");

        assertTrue(result);
        verify(invitationKeyRepository).findByInvitationKey("validKey");
    }

    @Test
    void validateInvitationKey_shouldReturnFalse_whenKeyIsExpired() {
        InvitationKeyEntity expiredKey = InvitationKeyEntity.builder()
                .invitationKey("expiredKey")
                .expirationTime(LocalDateTime.now().minusHours(1))
                .build();

        when(invitationKeyRepository.findByInvitationKey("expiredKey"))
                .thenReturn(Optional.of(expiredKey));

        boolean result = invitationKeyService.validateInvitationKey("expiredKey");

        assertFalse(result);
        verify(invitationKeyRepository).findByInvitationKey("expiredKey");
    }

    @Test
    void validateInvitationKey_shouldReturnFalse_whenKeyNotFound() {
        when(invitationKeyRepository.findByInvitationKey("none")).thenReturn(Optional.empty());

        boolean result = invitationKeyService.validateInvitationKey("none");

        assertFalse(result);
        verify(invitationKeyRepository).findByInvitationKey("none");
    }

    @Test
    void getServerIdByInvitationKey_shouldReturnId_whenKeyExists() {
        when(invitationKeyRepository.findByInvitationKey("key123"))
                .thenReturn(Optional.of(testKey));

        Long serverId = invitationKeyService.getServerIdByInvitationKey("key123");

        assertEquals(1L, serverId);
        verify(invitationKeyRepository).findByInvitationKey("key123");
    }

    @Test
    void getServerIdByInvitationKey_shouldThrowException_whenKeyNotFound() {
        when(invitationKeyRepository.findByInvitationKey("badKey")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> invitationKeyService.getServerIdByInvitationKey("badKey")
        );
        assertTrue(ex.getMessage().contains("Invitation key not found"));
        verify(invitationKeyRepository).findByInvitationKey("badKey");
    }
}
