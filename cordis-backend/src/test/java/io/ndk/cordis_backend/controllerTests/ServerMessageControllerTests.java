package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.ServerMessageController;
import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.MessageService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = ServerMessageController.class)
@AutoConfigureMockMvc(addFilters = true)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class ServerMessageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

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
    void testCreateMessage() throws Exception {
        MessageRequest messageRequest = MessageRequest.builder()
                .group(true)
                .userId(1L)
                .chatId(100L)
                .content("Hello group!")
                .build();

        MessageResponse messageResponse = MessageResponse.builder()
                .content("Hello group!")
                .build();

        Mockito.when(messageService.saveMessage(any(MessageRequest.class))).thenReturn(messageResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/server-messages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"group\":true,\"userId\":1,\"chatId\":100,\"content\":\"Hello group!\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"content\":\"Hello group!\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testGetMessages() throws Exception {
        MessageResponse messageResponse = MessageResponse.builder()
                .content("Hello group!")
                .build();
        List<MessageResponse> messageResponses = Collections.singletonList(messageResponse);

        Mockito.when(messageService.getGroupMessages(anyLong())).thenReturn(messageResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/server-messages/100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"content\":\"Hello group!\"}]"))
                .andDo(print());
    }
}
