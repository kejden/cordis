package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.impl.ServerChannelMapper;
import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.entity.*;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.*;
import io.ndk.cordis_backend.service.impl.ServerChannelServiceimpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private RoleRepository rolesRepository;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerChannelMapper mapper;

    @InjectMocks
    private ServerChannelServiceimpl channelService;

    @Test
    void testGetByServerId_Success() {
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").build();
        ServerChannelDto dto = ServerChannelDto.builder().id(1L).name("testChannel").build();
        when(channelRepository.findByServerId(1L)).thenReturn(
                Collections.singletonList(channel)
        );
        when(mapper.mapTo(channel)).thenReturn(dto);

        List<ServerChannelDto> channels = channelService.getByServerId(1L);

        assertFalse(channels.isEmpty());
        assertEquals(dto.getName(), channels.get(0).getName());
    }   

    @Test
    void testCreateServerChannel_Success_Moderator() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").build();
        ServerChannelDto dto = ServerChannelDto.builder().id(1L).name("testChannel").build();
        ServerEntity server = ServerEntity.builder()
                .id(1L)
                .name("testServer")
                .channels(Collections.singletonList(channel))
                .build();
        CreateServerChannel createServerChannel = CreateServerChannel.builder()
                .serverId(1L)
                .name("testChannel")
                .build();
        RoleEntity role = RoleEntity.builder().id(1L).name("MODERATOR").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(role));
        when(serverRepository.findById(createServerChannel.getServerId())).thenReturn(Optional.of(server));
        when(channelRepository.save(any(ServerChannelEntity.class))).thenReturn(channel);
        when(mapper.mapTo(channel)).thenReturn(dto);

        ServerChannelDto channelDto = channelService.create(createServerChannel, "user@example.com");

        assertNotNull(channelDto);
        assertEquals(dto.getName(), channelDto.getName());
    }

    @Test
    void testCreateServerChannel_Success_RoleOwner() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").build();
        ServerChannelDto dto = ServerChannelDto.builder().id(1L).name("testChannel").build();
        ServerEntity server = ServerEntity.builder()
                .id(1L)
                .name("testServer")
                .channels(Collections.singletonList(channel))
                .build();
        CreateServerChannel createServerChannel = CreateServerChannel.builder()
                .serverId(1L)
                .name("testChannel")
                .build();
        RoleEntity role = RoleEntity.builder().id(1L).name("OWNER").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(new RoleEntity()));
        when(rolesRepository.findByName("OWNER")).thenReturn(Optional.of(role));
        when(serverRepository.findById(createServerChannel.getServerId())).thenReturn(Optional.of(server));
        when(channelRepository.save(any(ServerChannelEntity.class))).thenReturn(channel);
        when(mapper.mapTo(channel)).thenReturn(dto);

        ServerChannelDto channelDto = channelService.create(createServerChannel, "user@example.com");

        assertNotNull(channelDto);
        assertEquals(dto.getName(), channelDto.getName());
    }

    @Test
    void testCreateServerChannel_NoPermission(){
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").build();
        ServerEntity server = ServerEntity.builder()
                .id(1L)
                .name("testServer")
                .channels(Collections.singletonList(channel))
                .build();
        CreateServerChannel createServerChannel = CreateServerChannel.builder()
                .serverId(1L)
                .name("testChannel")
                .build();
        RoleEntity role = RoleEntity.builder().id(1L).name("USER").build();
        RoleEntity roleMod = RoleEntity.builder().id(2L).name("MODERATOR").build();
        RoleEntity roleOwner = RoleEntity.builder().id(3L).name("OWNER").build();


        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(roleMod));
        when(rolesRepository.findByName("OWNER")).thenReturn(Optional.of(roleOwner));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.create(createServerChannel, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
    }

    @Test
    void testCreateChannel_NoUser_ThrowsException() {
        CreateServerChannel req = CreateServerChannel.builder().serverId(1L).name("New Channel").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> {channelService.create(req, "user@example.com");});
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
    }

    @Test
    void testCreateChannel_NoRole_ThrowsException() {
        CreateServerChannel req = CreateServerChannel.builder().serverId(1L).name("New Channel").build();
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), 1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> {channelService.create(req, "user@example.com");}
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_ROLE, ex.getErrorCode());
    }

    @Test
    void testUpdateChannel_Success_RoleModerator() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerEntity server = ServerEntity.builder().id(1L).build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("oldChannel").server(server).build();
        ServerChannelDto dto = ServerChannelDto.builder().id(1L).name("newChannel").build();
        CreateServerChannel updateRequest = CreateServerChannel.builder().serverId(1L).name("newChannel").build();
        RoleEntity role = RoleEntity.builder().id(1L).name("MODERATOR").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), updateRequest.getServerId())).thenReturn(Optional.of(mbrEntity)); // Ensure this returns a valid role
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(role));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(channelRepository.save(any(ServerChannelEntity.class))).thenReturn(channel);
        when(mapper.mapTo(channel)).thenReturn(dto);

        ServerChannelDto updatedChannel = channelService.update(1L, updateRequest, "user@example.com");

        assertNotNull(updatedChannel);
        assertEquals("newChannel", updatedChannel.getName());
    }

    @Test
    void testUpdateChannel_Success_RoleOwner() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerEntity server = ServerEntity.builder().id(1L).build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("oldChannel").server(server).build();
        ServerChannelDto dto = ServerChannelDto.builder().id(1L).name("newChannel").build();
        CreateServerChannel updateRequest = CreateServerChannel.builder().serverId(1L).name("newChannel").build();
        RoleEntity role = RoleEntity.builder().id(1L).name("OWNER").build();
        RoleEntity roleMod = RoleEntity.builder().id(2L).name("MODERATOR").build();

        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), updateRequest.getServerId())).thenReturn(Optional.of(mbrEntity)); // Ensure this returns a valid role
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(roleMod));
        when(rolesRepository.findByName("OWNER")).thenReturn(Optional.of(role));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(channelRepository.save(any(ServerChannelEntity.class))).thenReturn(channel);
        when(mapper.mapTo(channel)).thenReturn(dto);

        ServerChannelDto updatedChannel = channelService.update(1L, updateRequest, "user@example.com");

        assertNotNull(updatedChannel);
        assertEquals("newChannel", updatedChannel.getName());
    }

    @Test
    void testUpdateChannel_NoUser_ThrowsException() {
        CreateServerChannel req = CreateServerChannel.builder().serverId(1L).name("newChannel").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.update(1L, req, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
    }

    @Test
    void testUpdateChannel_NoRole_ThrowsException() {
        CreateServerChannel req = CreateServerChannel.builder().serverId(1L).name("newChannel").build();
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), 1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.update(1L, req, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_ROLE, ex.getErrorCode());
    }

    @Test
    void testDeleteChannel_NoSuchChannel() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(channelRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.delete(1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_CHANNEL, ex.getErrorCode());
    }

    @Test
    void testUpdateChannel_NoPermission() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("oldChannel").build();
        CreateServerChannel updateRequest = CreateServerChannel.builder().serverId(1L).name("newChannel").build();
        RoleEntity role = RoleEntity.builder().id(1L).name("USER").build();
        RoleEntity roleMod = RoleEntity.builder().id(1L).name("MODERATOR").build();
        RoleEntity roleOwner = RoleEntity.builder().id(1L).name("OWNER").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(channel.getServer())
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), updateRequest.getServerId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(roleMod));
        when(rolesRepository.findByName("OWNER")).thenReturn(Optional.of(roleOwner));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.update(1L, updateRequest, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
    }

    @Test
    void testUpdateChannel_NoSuchChannel() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        CreateServerChannel updateRequest = CreateServerChannel.builder().serverId(1L).name("newChannel").build();
        RoleEntity role = RoleEntity.builder().id(1L).name("MODERATOR").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(ServerEntity.builder().id(1L).build())
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), updateRequest.getServerId())).thenReturn(Optional.of(mbrEntity)); // Ensure this returns a valid role
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(role));
        when(channelRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.update(1L, updateRequest, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_CHANNEL, ex.getErrorCode());
    }

    @Test
    void testDeleteChannel_Success_RoleModerator() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerEntity server = ServerEntity.builder().id(1L).build(); // Add a valid server
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").server(server).build();
        RoleEntity role = RoleEntity.builder().id(1L).name("MODERATOR").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(role));

        assertDoesNotThrow(() -> channelService.delete(1L, "user@example.com"));
    }

    @Test
    void testDeleteChannel_Success_RoleOwner() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerEntity server = ServerEntity.builder().id(1L).build(); // Add a valid server
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").server(server).build();
        RoleEntity role = RoleEntity.builder().id(1L).name("MODERATOR").build();
        RoleEntity roleOwn = RoleEntity.builder().id(2L).name("OWNER").build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(roleOwn)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(role));
        when(rolesRepository.findByName("OWNER")).thenReturn(Optional.of(roleOwn));

        assertDoesNotThrow(() -> channelService.delete(1L, "user@example.com"));
    }

    @Test
    void testDeleteChannel_NoPermission() {
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerEntity server = ServerEntity.builder().id(1L).build();
        ServerChannelEntity channel = ServerChannelEntity.builder().id(1L).name("testChannel").server(server).build();
        RoleEntity role = RoleEntity.builder().id(1L).name("USER").build();
        RoleEntity roleMod = RoleEntity.builder().id(1L).name("MODERATOR").build();
        RoleEntity roleOwn = RoleEntity.builder().id(1L).name("OWNER").build();

        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .user(user)
                .server(server)
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(channel));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId())).thenReturn(Optional.of(mbrEntity));
        when(rolesRepository.findByName("MODERATOR")).thenReturn(Optional.of(roleMod));
        when(rolesRepository.findByName("OWNER")).thenReturn(Optional.of(roleOwn));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.delete(1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
    }

    @Test
    void testDeleteChannel_NoUser(){
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.delete(1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
    }

    @Test
    void testDeleteChannel_NoRole(){
        UserEntity user = UserEntity.builder().id(1L).email("user@example.com").build();
        ServerEntity server = ServerEntity.builder().id(1L).build();
        ServerChannelEntity serverChannelEntity = ServerChannelEntity.builder().server(server).id(1L).build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(channelRepository.findById(1L)).thenReturn(Optional.of(serverChannelEntity));
        when(memberRolesRepository.findByUserIdAndServerId(user.getId(), serverChannelEntity.getServer().getId())).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> channelService.delete(1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_ROLE, ex.getErrorCode());
    }
}