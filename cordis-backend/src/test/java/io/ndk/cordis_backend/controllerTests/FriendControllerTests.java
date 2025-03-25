package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.FriendController;
import io.ndk.cordis_backend.dto.request.FriendRequest;
import io.ndk.cordis_backend.dto.response.FriendResponse;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.FriendService;
import io.ndk.cordis_backend.service.JwtService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
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

    private Principal principal;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        principal = () -> "testUser@example.com";
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRequestFriend() throws Exception {
        FriendRequest request = new FriendRequest("friendUser");

        mockMvc.perform(post("/api/friend/request")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFriendResponses() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder()
                .id(1L)
                .state("ACCEPT")
                .build();

        List<FriendResponse> friendResponses = Collections.singletonList(friendResponse);
        when(friendService.getFriendResponse(anyString())).thenReturn(friendResponses);

        mockMvc.perform(get("/api/friend/responses")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("ACCEPT"));
    }

    @Test
    void testGetPendingFriendRequests() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder()
                .id(1L)
                .state("REQUEST")
                .build();

        when(friendService.getPendingFriendResponse(anyString()))
                .thenReturn(Collections.singletonList(friendResponse));

        mockMvc.perform(get("/api/friend/pending")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("REQUEST"));
    }

    @Test
    void testGetAwaitingFriendRequests() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder()
                .id(1L)
                .state("REQUEST")
                .build();

        when(friendService.getAwaitingFriendResponse(anyString()))
                .thenReturn(Collections.singletonList(friendResponse));

        mockMvc.perform(get("/api/friend/awaiting")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("REQUEST"));
    }

    @Test
    void testRefuseFriend() throws Exception {
        mockMvc.perform(delete("/api/friend/refuse/1")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void testAddFriend() throws Exception {
        mockMvc.perform(post("/api/friend/accept/1")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void testBanFriend() throws Exception {
        mockMvc.perform(post("/api/friend/ban/1")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void testLatestChats() throws Exception {
        FriendResponse friendResponse = FriendResponse.builder()
                .id(1L)
                .state("ACCEPT")
                .build();

        List<FriendResponse> friendResponses = Collections.singletonList(friendResponse);
        when(friendService.latestChats(anyString())).thenReturn(friendResponses);

        mockMvc.perform(get("/api/friend/latestChats")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("ACCEPT"));
    }
}