package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.Mappers.impl.RoleMapper;
import io.ndk.cordis_backend.Mappers.impl.ServerMapper;
import io.ndk.cordis_backend.Mappers.impl.UserDtoMapper;
import io.ndk.cordis_backend.dto.RoleDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ServerServiceTest {

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
    void joinServer_shouldThrowException_whenInvitationKeyIsNotValid() {
        when(invitationKeyService.validateInvitationKey("invalidKey")).thenReturn(false);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> serverServiceImpl.joinServer("invalidKey", "user@example.com")
        );
        assertEquals(BusinessErrorCodes.INCORRECT_INVITE, ex.getErrorCode());
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
}