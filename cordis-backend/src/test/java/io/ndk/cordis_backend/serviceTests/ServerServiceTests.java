package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.impl.RoleMapper;
import io.ndk.cordis_backend.Mappers.impl.ServerMapper;
import io.ndk.cordis_backend.Mappers.impl.UserDtoMapper;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import io.ndk.cordis_backend.dto.response.ServerResponse;
import io.ndk.cordis_backend.dto.response.UserRole;
import io.ndk.cordis_backend.entity.MemberRolesEntity;
import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.*;
import io.ndk.cordis_backend.service.FileService;
import io.ndk.cordis_backend.service.InvitationKeyService;
import io.ndk.cordis_backend.service.impl.ServerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ServerServiceTests {

    @Mock
    private ServerRepository serverRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InvitationKeyService invitationKeyService;
    @Mock
    private MemberRolesRepository mbrRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private InvitationKeyRepository InvitationKeyRepository;
    @Mock
    private FileService fileService;
    @Mock
    private ServerMapper mapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private UserDtoMapper userMapper;

    @InjectMocks
    private ServerServiceImpl serverServiceImpl;

    @Test
    void getServerById_shouldReturnServerDto_whenServerExists() {
        ServerEntity serverEntity = ServerEntity.builder()
                .id(1L)
                .build();
        when(serverRepository.findById(1L)).thenReturn(Optional.of(serverEntity));

        ServerDto serverDto =  ServerDto.builder()
                .id(1L)
                .build();
        when(mapper.mapTo(serverEntity)).thenReturn(serverDto);

        ServerDto result = serverServiceImpl.getServerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(serverRepository).findById(1L);
        verify(mapper).mapTo(serverEntity);
    }

    @Test
    void getServerById_shouldThrowException_whenServerNotFound() {
        when(serverRepository.findById(2L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.getServerById(2L)
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_SERVER, ex.getErrorCode());
    }

    @Test
    void joinServer(){
        RoleEntity role = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .build();
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .build();
        ServerEntity serverEntity = ServerEntity.builder()
                .id(1L)
                .name("Test Server")
                .build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .id(1L)
                .server(serverEntity)
                .user(user)
                .role(role)
                .build();

        when(invitationKeyService.validateInvitationKey("validKey")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(invitationKeyService.getServerIdByInvitationKey("validKey")).thenReturn(1L);
        when(mbrRepository.existsByUserIdAndServerId(1L, 1L)).thenReturn(false);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(serverEntity));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(mbrRepository.save(any(MemberRolesEntity.class))).thenReturn(mbrEntity);

        String res = serverServiceImpl.joinServer("validKey", "user@example.com");
        assertEquals(res, "SUCCESS");
    }

    @Test
    void joinServer_shouldThrowException_whenInvitationKeyIsNotValid() {
        when(invitationKeyService.validateInvitationKey("invalidKey")).thenReturn(false);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.joinServer("invalidKey", "user@example.com")
        );
        assertEquals(BusinessErrorCodes.INCORRECT_INVITE, ex.getErrorCode());
    }

    @Test
    void joinServer_shouldThrowException_whenUserIsInvalid(){
        when(invitationKeyService.validateInvitationKey("validKey")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.joinServer("validKey", "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
    }

    @Test
    void joinServer_shouldThrowException_whenUserRoleExists(){
        UserEntity owner = UserEntity.builder()
                .id(1L)
                .email("owner@example.com")
                .build();

        when(invitationKeyService.validateInvitationKey("validKey")).thenReturn(true);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(invitationKeyService.getServerIdByInvitationKey("validKey")).thenReturn(1L);
        when(mbrRepository.existsByUserIdAndServerId(1L, 1L)).thenReturn(true);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.joinServer("validKey", "owner@example.com")
        );
        assertEquals(BusinessErrorCodes.USER_ROLE_EXISTS, ex.getErrorCode());
    }

    @Test
    void joinServer_shouldThrowException_whenServerDoesntExist(){
        UserEntity owner = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        when(invitationKeyService.validateInvitationKey("validKey")).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(owner));
        when(invitationKeyService.getServerIdByInvitationKey("validKey")).thenReturn(1L);
        when(mbrRepository.existsByUserIdAndServerId(1L, 1L)).thenReturn(false);


        when(serverRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.joinServer("validKey", "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_SERVER, ex.getErrorCode());
    }

    @Test
    void getUsersRoleForServer_shouldReturnUserRole(){
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .build();
        ServerEntity server = ServerEntity.builder()
                .id(1L)
                .name("Test Server")
                .build();
        RoleEntity role = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .build();
        MemberRolesEntity mbrEntity = MemberRolesEntity.builder()
                .role(role)
                .user(user)
                .server(server)
                .build();


        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(mbrRepository.findByUserIdAndServerId(1L, 1L)).thenReturn(Optional.of(mbrEntity));

        RoleEntity roleEntity = serverServiceImpl.getUsersRoleForServer(1L, "user@example.com");

        assertNotNull(roleEntity);
    }

    @Test
    void getUsersRoleForServer_shouldThrowException_whenUserDoesntExist(){
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.getUsersRoleForServer(1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());

    }

    @Test
    void createServer_shouldSaveServerAndOwnerRole() {
        ServerCreate serverCreate = ServerCreate.builder()
                .name("Test Server")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .email("owner@example.com")
                .build();
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(userEntity));

        RoleEntity roleEntity = RoleEntity.builder()
                .name("OWNER")
                .build();
        when(roleRepository.findByName("OWNER")).thenReturn(Optional.of(roleEntity));

        ServerEntity serverEntity = ServerEntity.builder()
                .name("Test Server")
                .build();
        when(serverRepository.save(any(ServerEntity.class))).thenReturn(serverEntity);

        when(mapper.mapTo(serverEntity)).thenReturn(new ServerDto());

        ServerDto result = serverServiceImpl.createServer(serverCreate, "owner@example.com");

        assertNotNull(result);
        verify(serverRepository).save(any(ServerEntity.class));
        verify(mbrRepository).save(any(MemberRolesEntity.class));
    }

    @Test
    void updateServer(){
        UserEntity owner = UserEntity.builder()
                .email("owner@example.com")
                .build();

        ServerEntity existingServer = ServerEntity.builder()
                .owner(owner)
                .name("Old Name")
                .build();

        ServerDto dto = ServerDto.builder()
                .name("New Name")
                .build();

        when(serverRepository.findById(1L)).thenReturn(Optional.of(existingServer));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(serverRepository.save(any(ServerEntity.class))).thenReturn(existingServer);
        when(mapper.mapTo(existingServer)).thenReturn(dto);

        ServerDto serverDto = serverServiceImpl.updateServer(1L, dto, "owner@example.com");

        assertNotNull(serverDto);
//        verify(serverRepository).save(any(ServerEntity.class));
        assertEquals(dto.getName(), serverDto.getName());
    }

    @Test
    void updateServer_shouldThrowException_whenUserIsNotOwner() {
        UserEntity owner = UserEntity.builder()
                .email("owner@example.com")
                .build();

        ServerEntity existingServer = ServerEntity.builder()
                .owner(owner)
                .build();

        when(serverRepository.findById(1L)).thenReturn(Optional.of(existingServer));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new UserEntity()));

        ServerDto dto = ServerDto.builder()
                .name("New Name")
                .build();

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.updateServer(1L, dto, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
    }

    @Test
    void deleteServer_shouldDelete_whenCallerIsOwner() {
        UserEntity owner = UserEntity.builder()
                .email("owner@example.com")
                .build();

        ServerEntity existingServer = ServerEntity.builder()
                .owner(owner)
                .build();

        existingServer.setOwner(owner);

        when(serverRepository.existsById(1L)).thenReturn(true);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(existingServer));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

        serverServiceImpl.deleteServer(1L, "owner@example.com");

        verify(mbrRepository).deleteByServerId(1L);
        verify(serverRepository).deleteById(1L);
        verify(InvitationKeyRepository).deleteByServerId(1L);
    }

    @Test
    void deleteServer_noPermission() {
        UserEntity userEntity = UserEntity.builder()
                .email("user@example.com")
                .build();
        UserEntity owner = UserEntity.builder()
                .email("owner@example.com")
                .build();
        ServerEntity existingServer = ServerEntity.builder()
                .owner(owner)
                .build();

        existingServer.setOwner(owner);

        when(serverRepository.existsById(1L)).thenReturn(true);
        when(serverRepository.findById(1L)).thenReturn(Optional.of(existingServer));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.deleteServer(1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
    }

    @Test
    void updateServerImage_shouldUpdateImage_whenUserIsOwner() {
        UserEntity owner = UserEntity.builder()
                .id(1L)
                .email("owner@example.com")
                .build();
        ServerEntity serverEntity = ServerEntity.builder()
                .id(1L)
                .name("MyServer")
                .owner(owner)
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(serverRepository.findById(1L)).thenReturn(Optional.of(serverEntity));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(fileService.updateFile(file, "MyServer")).thenReturn("image/path.png");

        String result = serverServiceImpl.updateServerImage(file, 1L, "owner@example.com");

        assertTrue(result.contains("image/path.png"));
        verify(serverRepository).save(any(ServerEntity.class));
    }

    @Test
    void updateServerImage_shouldUpdateImage_whenUserIsNotOwner() {
        UserEntity owner = UserEntity.builder()
                .id(1L)
                .email("owner@example.com")
                .build();
        UserEntity user = UserEntity.builder()
                .id(2L)
                .email("user@example.com")
                .build();
        ServerEntity serverEntity = ServerEntity.builder()
                .id(1L)
                .name("MyServer")
                .owner(owner)
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(serverRepository.findById(1L)).thenReturn(Optional.of(serverEntity));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.updateServerImage(file, 1L, "user@example.com")
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
    }

    @Test
    void getAllServerOfUser_shouldReturnServers_whenUserExistsAndHasMemberships() {
        String email = "user@example.com";
        Long userId = 1L;

        UserEntity user = UserEntity.builder().id(userId).build();
        ServerEntity server1 = ServerEntity.builder().id(1L).name("Server 1").build();
        ServerEntity server2 = ServerEntity.builder().id(2L).name("Server 2").build();
        RoleEntity role1 = RoleEntity.builder().id(1L).name("USER").build();
        RoleEntity role2 = RoleEntity.builder().id(2L).name("ADMIN").build();

        MemberRolesEntity mb1 = MemberRolesEntity.builder()
                .server(server1)
                .role(role1)
                .build();
        MemberRolesEntity mb2 = MemberRolesEntity.builder()
                .server(server2)
                .role(role2)
                .build();

        List<MemberRolesEntity> memberships = List.of(mb1, mb2);

        ServerResponse response1 = ServerResponse.builder()
                .server(mapper.mapTo(server1))
                .role(roleMapper.mapTo(role1))
                .build();
        ServerResponse response2 = ServerResponse.builder()
                .server(mapper.mapTo(server2))
                .role(roleMapper.mapTo(role2))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mbrRepository.findByUserId(userId)).thenReturn(memberships);
        when(mapper.mapTo(server1)).thenReturn(response1.getServer());
        when(mapper.mapTo(server2)).thenReturn(response2.getServer());
        when(roleMapper.mapTo(role1)).thenReturn(response1.getRole());
        when(roleMapper.mapTo(role2)).thenReturn(response2.getRole());

        List<ServerResponse> result = serverServiceImpl.getAllServerOfUser(email);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
        verify(userRepository, times(1)).findByEmail(email);
        verify(mbrRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getAllServerOfUser_shouldThrowException_whenUserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            serverServiceImpl.getAllServerOfUser(email);
        });

        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, exception.getErrorCode());
        verify(userRepository, times(1)).findByEmail(email);
        verify(mbrRepository, never()).findByUserId(any());
    }

    @Test
    void getAllServerOfUser_shouldReturnEmptyList_whenUserHasNoMemberships() {
        String email = "user@example.com";
        Long userId = 1L;

        UserEntity user = UserEntity.builder().id(userId).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mbrRepository.findByUserId(userId)).thenReturn(List.of());

        List<ServerResponse> result = serverServiceImpl.getAllServerOfUser(email);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
        verify(mbrRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUsersOfServer_shouldReturnListOfUserRoles_whenServerHasMembers() {
        Long serverId = 1L;

        UserEntity user1 = UserEntity.builder().id(1L).email("user1@example.com").build();
        UserEntity user2 = UserEntity.builder().id(2L).email("user2@example.com").build();

        RoleEntity role1 = RoleEntity.builder().id(1L).name("USER").build();
        RoleEntity role2 = RoleEntity.builder().id(2L).name("ADMIN").build();

        MemberRolesEntity mb1 = MemberRolesEntity.builder()
                .user(user1)
                .role(role1)
                .build();
        MemberRolesEntity mb2 = MemberRolesEntity.builder()
                .user(user2)
                .role(role2)
                .build();

        List<MemberRolesEntity> memberRoles = List.of(mb1, mb2);

        UserDto userDto1 = UserDto.builder().id(1L).email("user1@example.com").build();
        UserDto userDto2 = UserDto.builder().id(2L).email("user2@example.com").build();

        when(mbrRepository.findByServerId(serverId)).thenReturn(memberRoles);
        when(userMapper.mapTo(user1)).thenReturn(userDto1);
        when(userMapper.mapTo(user2)).thenReturn(userDto2);

        List<UserRole> result = serverServiceImpl.getUsersOfServer(serverId);

        assertNotNull(result);
        assertEquals(2, result.size());

        UserRole userRole1 = result.get(0);
        assertEquals(userDto1, userRole1.getUser());
        assertEquals(role1, userRole1.getRole());

        UserRole userRole2 = result.get(1);
        assertEquals(userDto2, userRole2.getUser());
        assertEquals(role2, userRole2.getRole());

        verify(mbrRepository, times(1)).findByServerId(serverId);
        verify(userMapper, times(1)).mapTo(user1);
        verify(userMapper, times(1)).mapTo(user2);
    }

    @Test
    void getUsersOfServer_shouldReturnEmptyList_whenServerHasNoMembers() {
        Long serverId = 1L;

        when(mbrRepository.findByServerId(serverId)).thenReturn(Collections.emptyList());

        List<UserRole> result = serverServiceImpl.getUsersOfServer(serverId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mbrRepository, times(1)).findByServerId(serverId);
        verify(userMapper, never()).mapTo(any());
    }
}