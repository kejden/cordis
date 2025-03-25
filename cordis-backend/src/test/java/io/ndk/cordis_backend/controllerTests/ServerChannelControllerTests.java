package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.ServerChannelController;
import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.ServerChannelService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ServerChannelController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class ServerChannelControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServerChannelService serverChannelService;

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
    void testGetServerChannel() throws Exception {
        ServerChannelDto channel = ServerChannelDto.builder()
                .id(1L)
                .name("Test Channel")
                .build();

        when(serverChannelService.getByServerId(1L))
                .thenReturn(Collections.singletonList(channel));

        mockMvc.perform(get("/api/server-channel/1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Channel"));
    }

    @Test
    void testCreateServerChannel() throws Exception {
        ServerChannelDto channel = ServerChannelDto.builder()
                .id(1L)
                .name("Test Channel")
                .build();

        CreateServerChannel createRequest = CreateServerChannel.builder()
                .name("Test Channel")
                .serverId(1L)
                .build();

        when(serverChannelService.create(any(CreateServerChannel.class), anyString()))
                .thenReturn(channel);

        mockMvc.perform(post("/api/server-channel")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Channel"));
    }

    @Test
    void testUpdateServerChannel() throws Exception {
        ServerChannelDto channel = ServerChannelDto.builder()
                .id(1L)
                .name("Updated Channel")
                .build();

        CreateServerChannel updateRequest = CreateServerChannel.builder()
                .name("Updated Channel")
                .serverId(1L)
                .build();

        when(serverChannelService.update(eq(1L), any(CreateServerChannel.class), anyString()))
                .thenReturn(channel);

        mockMvc.perform(put("/api/server-channel/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Channel"));
    }

    @Test
    void testDeleteServerChannel() throws Exception {
        mockMvc.perform(delete("/api/server-channel/1")
                        .principal(principal))
                .andExpect(status().isNoContent());
    }
}