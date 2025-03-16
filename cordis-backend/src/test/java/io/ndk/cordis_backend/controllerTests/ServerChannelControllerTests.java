package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.ServerChannelController;
import io.ndk.cordis_backend.dto.ServerChannelDto;
import io.ndk.cordis_backend.dto.request.CreateServerChannel;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.ServerChannelService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = ServerChannelController.class)
@AutoConfigureMockMvc(addFilters = true)
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


    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetServerChannel() throws Exception {
        ServerChannelDto serverChannelDto = ServerChannelDto.builder().id(1L).name("Test Channel").build();
        List<ServerChannelDto> serverChannelDtos = Collections.singletonList(serverChannelDto);
        when(serverChannelService.getByServerId(anyLong())).thenReturn(serverChannelDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server-channel/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"name\":\"Test Channel\"}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testCreateServerChannel() throws Exception {
        ServerChannelDto serverChannelDto = ServerChannelDto.builder().id(1L).name("Test Channel").build();
        when(serverChannelService.create(any(CreateServerChannel.class), anyString())).thenReturn(serverChannelDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/server-channel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Channel\",\"serverId\":1}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"Test Channel\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testUpdateServerChannel() throws Exception {
        ServerChannelDto serverChannelDto = ServerChannelDto.builder().id(1L).name("Updated Channel").build();
        when(serverChannelService.update(anyLong(), any(CreateServerChannel.class), anyString())).thenReturn(serverChannelDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/server-channel/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Channel\",\"serverId\":1}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"Updated Channel\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testDeleteServerChannel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/server-channel/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }
}
