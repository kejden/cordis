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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = ServerController.class)
@AutoConfigureMockMvc(addFilters = true)
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

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testAcceptServer() throws Exception {
        when(serverService.joinServer(anyString(), anyString())).thenReturn("SUCCESS");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/server/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"invitationKey\""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("SUCCESS"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetServerById() throws Exception {
        ServerDto serverDto = ServerDto.builder().id(1L).name("Test Server").build();
        when(serverService.getServerById(anyLong())).thenReturn(serverDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"Test Server\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetUserRoles() throws Exception {
        RoleEntity roleEntity = RoleEntity.builder().id(1L).name("USER").build();
        when(serverService.getUsersRoleForServer(anyLong(), anyString())).thenReturn(roleEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server/1/role"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"USER\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetServerUsers() throws Exception {
        UserRole userRole = UserRole.builder()
                .user(UserDto.builder().id(1L).email("user1@example.com").build())
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .build();
        List<UserRole> userRoles = Collections.singletonList(userRole);
        when(serverService.getUsersOfServer(anyLong())).thenReturn(userRoles);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server/1/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        "[{\"user\":" +
                                "{\"id\":1," +
                                "\"email\":\"user1@example.com\"}," +
                                "\"role\":{\"id\":1,\"name\":\"USER\"}}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetAllServersOfUser() throws Exception {
        ServerResponse serverResponse = ServerResponse.builder()
                .server(ServerDto.builder().id(1L).name("Test Server").build())
                .role(RoleDto.builder().id(1L).name("OWNER").build())
                .build();
        List<ServerResponse> serverResponses = Collections.singletonList(serverResponse);
        when(serverService.getAllServerOfUser(anyString())).thenReturn(serverResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        "[{\"server\":{\"id\":1,\"name\":\"Test Server\"}," +
                                "\"role\":{\"id\":1,\"name\":\"OWNER\"}}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testCreateServer() throws Exception {
        ServerDto serverDto = ServerDto.builder().id(1L).name("Test Server").build();
        when(serverService.createServer(any(ServerCreate.class), anyString())).thenReturn(serverDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/server")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Server\",\"image\":\"testImage.png\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"Test Server\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testUpdateServer() throws Exception {
        ServerDto serverDto = ServerDto.builder().id(1L).name("Updated Server").build();
        when(serverService.updateServer(anyLong(), any(ServerDto.class), anyString())).thenReturn(serverDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/server/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Server\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"Updated Server\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testDeleteServer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/server/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
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
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("imagePath"))
                .andDo(print());
    }
}