package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.impl.MemberMapper;
import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.dto.RoleDto;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.CreateMemberRoles;
import io.ndk.cordis_backend.entity.MemberRolesEntity;
import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.MemberRolesRepository;
import io.ndk.cordis_backend.repository.RoleRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.impl.MemberRolesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MemberRolesServiceTests {

    @Mock
    private MemberRolesRepository memberRolesRepository;
    @Mock
    private MemberMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private MemberRolesServiceImpl memberRolesService;

    @Test
    void createMemberRoles_Success() {
        CreateMemberRoles dto = CreateMemberRoles.builder()
                .serverId(1L).memberId(2L).build();
        UserEntity user = UserEntity.builder()
                .id(2L).build();
        UserDto userDto = UserDto.builder()
                .id(2L).build();
        ServerEntity server = ServerEntity.builder()
                .id(1L).build();
        ServerDto serverDto  = ServerDto.builder()
                .id(1L).build();
        RoleEntity role = RoleEntity.builder()
                .name("USER").build();
        RoleDto roleDto = RoleDto.builder()
                .name("USER").build();
        MemberRolesEntity savedEntity = MemberRolesEntity.builder()
                .id(10L).user(user).server(server).role(role).build();
        MemberRolesDto mbrDto = MemberRolesDto.builder()
                .role(roleDto).user(userDto).server(serverDto).build();

        when(memberRolesRepository.existsByUserIdAndServerId(2L, 1L)).thenReturn(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(memberRolesRepository.save(any(MemberRolesEntity.class))).thenReturn(savedEntity);
        when(mapper.mapTo(any(MemberRolesEntity.class))).thenReturn(mbrDto);

        MemberRolesDto result = memberRolesService.createMemberRoles(dto);

        assertNotNull(result);
        assertEquals("USER", result.getRole().getName());
        verify(memberRolesRepository).save(any(MemberRolesEntity.class));
    }

    @Test
    void createMemberRoles_ShouldThrowExceptionIfMemberExists() {
        CreateMemberRoles dto = CreateMemberRoles.builder().serverId(1L).memberId(2L).build();
        when(memberRolesRepository.existsByUserIdAndServerId(2L, 1L)).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> memberRolesService.createMemberRoles(dto));
        assertEquals(BusinessErrorCodes.USER_ROLE_EXISTS, ex.getErrorCode());
        verify(memberRolesRepository, never()).save(any(MemberRolesEntity.class));
    }

    @Test
    void updateMemberRoles_Success() {
        CreateMemberRoles dto = CreateMemberRoles.builder().serverId(1L).memberId(2L).role("ADMIN").build();
        MemberRolesEntity existing = MemberRolesEntity.builder().id(5L).build();
        when(memberRolesRepository.findByUserIdAndServerId(2L, 1L)).thenReturn(Optional.of(existing));
        RoleEntity role = RoleEntity.builder().name("ADMIN").build();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        memberRolesService.updateMemberRoles(dto);

        verify(memberRolesRepository).save(existing);
        assertEquals("ADMIN", existing.getRole().getName());
    }

    @Test
    void updateMemberRoles_NoMember() {
        CreateMemberRoles dto = CreateMemberRoles.builder().serverId(1L).memberId(2L).role("ADMIN").build();
        when(memberRolesRepository.findByUserIdAndServerId(2L, 1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> memberRolesService.updateMemberRoles(dto));
        assertEquals(BusinessErrorCodes.NO_MEMBER, ex.getErrorCode());
        verify(memberRolesRepository, never()).save(any(MemberRolesEntity.class));
    }

    @Test
    void deleteMemberRoles_Success() {
        when(memberRolesRepository.existsByUserIdAndServerId(2L, 1L)).thenReturn(true);
        doNothing().when(memberRolesRepository).deleteByUserIdAndServerId(2L, 1L);

        assertDoesNotThrow(() -> memberRolesService.deleteMemberRoles(1L, 2L));
        verify(memberRolesRepository).deleteByUserIdAndServerId(2L, 1L);
    }

    @Test
    void deleteMemberRoles_NoMember() {
        when(memberRolesRepository.existsByUserIdAndServerId(2L, 1L)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class, () -> memberRolesService.deleteMemberRoles(1L, 2L));
        assertEquals(BusinessErrorCodes.NO_MEMBER, ex.getErrorCode());
        verify(memberRolesRepository, never()).deleteByUserIdAndServerId(2L, 1L);
    }
}
