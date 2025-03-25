package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.MemberRolesController;
import io.ndk.cordis_backend.dto.MemberRolesDto;
import io.ndk.cordis_backend.dto.RoleDto;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.CreateMemberRoles;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.MemberRolesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = MemberRolesController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class MemberRolesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRolesService memberRolesService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private io.ndk.cordis_backend.service.impl.customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CookieService cookieService;

    private Principal principal;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        principal = () -> "testUser@example.com";
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateMemberRoles() throws Exception {
        ServerDto serverDto = ServerDto.builder()
                .id(1L)
                .name("Test Server")
                .build();

        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("user@example.com")
                .build();

        RoleDto roleDto = RoleDto.builder()
                .name("USER")
                .build();

        MemberRolesDto memberRolesDto = MemberRolesDto.builder()
                .server(serverDto)
                .user(userDto)
                .role(roleDto)
                .build();

        CreateMemberRoles createRequest = CreateMemberRoles.builder()
                .serverId(1L)
                .memberId(2L)
                .role("USER")
                .build();

        when(memberRolesService.createMemberRoles(any(CreateMemberRoles.class))).thenReturn(memberRolesDto);

        mockMvc.perform(post("/api/member-roles")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.server.id").value(1))
                .andExpect(jsonPath("$.server.name").value("Test Server"))
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.role.name").value("USER"));
    }

    @Test
    void testUpdateMemberRoles() throws Exception {
        ServerDto serverDto = ServerDto.builder()
                .id(1L)
                .name("Test Server")
                .build();

        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("user@example.com")
                .build();

        RoleDto roleDto = RoleDto.builder()
                .name("ADMIN")
                .build();

        MemberRolesDto memberRolesDto = MemberRolesDto.builder()
                .server(serverDto)
                .user(userDto)
                .role(roleDto)
                .build();

        CreateMemberRoles updateRequest = CreateMemberRoles.builder()
                .serverId(1L)
                .memberId(2L)
                .role("ADMIN")
                .build();

        when(memberRolesService.updateMemberRoles(any(CreateMemberRoles.class))).thenReturn(memberRolesDto);

        mockMvc.perform(put("/api/member-roles")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.server.id").value(1))
                .andExpect(jsonPath("$.server.name").value("Test Server"))
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.role.name").value("ADMIN"));
    }

    @Test
    void testDeleteMemberRoles() throws Exception {
        mockMvc.perform(delete("/api/member-roles/1/2")
                        .principal(principal))
                .andExpect(status().isNoContent());
    }
}