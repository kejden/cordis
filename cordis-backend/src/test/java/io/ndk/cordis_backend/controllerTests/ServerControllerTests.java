package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.ServerController;
import io.ndk.cordis_backend.dto.RoleDto;
import io.ndk.cordis_backend.dto.ServerDto;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.ServerCreate;
import io.ndk.cordis_backend.dto.response.ServerResponse;
import io.ndk.cordis_backend.dto.response.UserRole;
import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.ServerService;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ServerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class ServerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServerService serverService;

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
    void testAcceptServer() throws Exception {
        when(serverService.joinServer(anyString(), anyString())).thenReturn("SUCCESS");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/server/join")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("invitationKey")))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));
    }

    @Test
    void testGetServerById() throws Exception {
        ServerDto serverDto = ServerDto.builder()
                .id(1L)
                .name("Test Server")
                .build();

        when(serverService.getServerById(anyLong())).thenReturn(serverDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server/1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Server"));
    }

    @Test
    void testGetUserRoles() throws Exception {
        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .build();

        when(serverService.getUsersRoleForServer(anyLong(), anyString())).thenReturn(roleEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server/1/role")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("USER"));
    }

    @Test
    void testGetServerUsers() throws Exception {
        UserRole userRole = UserRole.builder()
                .user(UserDto.builder().id(1L).email("user1@example.com").build())
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .build();

        List<UserRole> userRoles = Collections.singletonList(userRole);
        when(serverService.getUsersOfServer(anyLong())).thenReturn(userRoles);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server/1/users")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(1))
                .andExpect(jsonPath("$[0].user.email").value("user1@example.com"))
                .andExpect(jsonPath("$[0].role.id").value(1))
                .andExpect(jsonPath("$[0].role.name").value("USER"));
    }

    @Test
    void testGetAllServersOfUser() throws Exception {
        ServerResponse serverResponse = ServerResponse.builder()
                .server(ServerDto.builder().id(1L).name("Test Server").build())
                .role(RoleDto.builder().id(1L).name("OWNER").build())
                .build();

        List<ServerResponse> serverResponses = Collections.singletonList(serverResponse);
        when(serverService.getAllServerOfUser(anyString())).thenReturn(serverResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].server.id").value(1))
                .andExpect(jsonPath("$[0].server.name").value("Test Server"))
                .andExpect(jsonPath("$[0].role.id").value(1))
                .andExpect(jsonPath("$[0].role.name").value("OWNER"));
    }

    @Test
    void testCreateServer() throws Exception {
        ServerDto serverDto = ServerDto.builder()
                .id(1L)
                .name("Test Server")
                .build();

        ServerCreate serverCreate = ServerCreate.builder()
                .name("Test Server")
                .image("testImage.png")
                .build();

        when(serverService.createServer(any(ServerCreate.class), anyString())).thenReturn(serverDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/server")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serverCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Server"));
    }

    @Test
    void testUpdateServer() throws Exception {
        ServerDto serverDto = ServerDto.builder()
                .id(1L)
                .name("Updated Server")
                .build();

        ServerDto updateRequest = ServerDto.builder()
                .name("Updated Server")
                .build();

        when(serverService.updateServer(anyLong(), any(ServerDto.class), anyString())).thenReturn(serverDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/server/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Server"));
    }

    @Test
    void testDeleteServer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/server/1")
                        .principal(principal))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUploadImageToFileSystem() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "testImage.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        when(serverService.updateServerImage(any(), anyLong(), anyString())).thenReturn("imagePath");

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/server/1/image")
                        .file(imageFile)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("imagePath"));
    }
}