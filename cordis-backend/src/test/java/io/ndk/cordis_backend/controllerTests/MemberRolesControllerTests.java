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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = MemberRolesController.class)
@AutoConfigureMockMvc(addFilters = true)
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

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testCreateMemberRoles() throws Exception {
        ServerDto serverDto = ServerDto.builder().id(1L).name("Test Server").build();
        UserDto userDto = UserDto.builder().id(2L).email("user@example.com").build();
        RoleDto roleDto = RoleDto.builder().name("USER").build();
        MemberRolesDto memberRolesDto = MemberRolesDto.builder()
                .server(serverDto)
                .user(userDto)
                .role(roleDto)
                .build();
        Mockito.when(memberRolesService.createMemberRoles(any(CreateMemberRoles.class))).thenReturn(memberRolesDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/member-roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"serverId\":1,\"memberId\":2,\"role\":\"USER\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"server\":{\"id\":1,\"name\":\"Test Server\"},\"user\":{\"id\":2,\"email\":\"user@example.com\"},\"role\":{\"name\":\"USER\"}}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testUpdateMemberRoles() throws Exception {
        ServerDto serverDto = ServerDto.builder().id(1L).name("Test Server").build();
        UserDto userDto = UserDto.builder().id(2L).email("user@example.com").build();
        RoleDto roleDto = RoleDto.builder().name("ADMIN").build();
        MemberRolesDto memberRolesDto = MemberRolesDto.builder()
                .server(serverDto)
                .user(userDto)
                .role(roleDto)
                .build();
        Mockito.when(memberRolesService.updateMemberRoles(any(CreateMemberRoles.class))).thenReturn(memberRolesDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/member-roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"serverId\":1,\"memberId\":2,\"role\":\"ADMIN\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"server\":{\"id\":1,\"name\":\"Test Server\"},\"user\":{\"id\":2,\"email\":\"user@example.com\"},\"role\":{\"name\":\"ADMIN\"}}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testDeleteMemberRoles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/member-roles/1/2"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }
}
