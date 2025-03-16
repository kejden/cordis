package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.FriendController;
import io.ndk.cordis_backend.dto.request.FriendRequest;
import io.ndk.cordis_backend.dto.response.FriendResponse;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.FriendService;
import io.ndk.cordis_backend.service.JwtService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = FriendController.class)
@AutoConfigureMockMvc(addFilters = true)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class FriendControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

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
    void testRequestFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/friend/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"friendUser\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetFriendResponses() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder().id(1L).state("ACCEPT").build();
        List<FriendResponse> friendResponses = Collections.singletonList(friendResponse);
        Mockito.when(friendService.getFriendResponse(anyString())).thenReturn(friendResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/friend/responses"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"state\":\"ACCEPT\"}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetPendingFriendRequests() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder().id(1L).state("REQUEST").build();
        List<FriendResponse> friendResponses = Collections.singletonList(friendResponse);
        Mockito.when(friendService.getPendingFriendResponse(anyString())).thenReturn(friendResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/friend/pending"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"state\":\"REQUEST\"}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetAwaitingFriendRequests() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder().id(1L).state("REQUEST").build();
        List<FriendResponse> friendResponses = Collections.singletonList(friendResponse);
        Mockito.when(friendService.getAwaitingFriendResponse(anyString())).thenReturn(friendResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/friend/awaiting"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"state\":\"REQUEST\"}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testRefuseFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/friend/refuse/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testAddFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/friend/accept/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testBanFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/friend/ban/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testLatestChats() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder().id(1L).state("ACCEPT").build();
        List<FriendResponse> friendResponses = Collections.singletonList(friendResponse);
        Mockito.when(friendService.latestChats(anyString())).thenReturn(friendResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/friend/latestChats"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"state\":\"ACCEPT\"}]"))
                .andDo(print());
    }
}
