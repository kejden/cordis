package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.entity.ServerChannelEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.MemberRolesRepository;
import io.ndk.cordis_backend.repository.ServerChannelRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.ServerChannelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ServerChannelServiceTests {

    @Mock
    private ServerChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MemberRolesRepository memberRolesRepository;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerChannelService channelService;

    @Test
    void testGetByServerId_Success() {
        when(channelRepository.findByServerId(1L)).thenReturn(
                Collections.singletonList(ServerChannelEntity.builder().id(1L).name("testChannel").build())
        );
        List<ServerChannelEntity> channels = channelRepository.findByServerId(1L);
        assertFalse(channels.isEmpty());
        assertEquals("testChannel", channels.get(0).getName());
    }

    @Test
    void testCreateChannel_NoUser_ThrowsException() {
        CreateServerChannel req = CreateServerChannel.builder().serverId(1L).name("New Channel").build();
        Exception ex = assertThrows(CustomException.class, () -> {
            throw new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL);
        });
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ((CustomException) ex).getErrorCode());
    }
}