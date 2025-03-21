package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.controller.MessageController;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MessageControllerTests {

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
    void testCreateMessage_Success() throws Exception {
        MessageResponse mockResponse = MessageResponse.builder()
                .content("Hello!")
                .build();

        Mockito.when(messageService.saveMessage(any(MessageRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/messages/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"group\":false,\"userId\":1,\"chatId\":123,\"content\":\"Hello!\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello!"));
    }

    @Test
    void testGetMessages_Success() throws Exception {
        Mockito.when(messageService.getMessages(anyLong())).thenReturn(
                java.util.List.of(
                        MessageResponse.builder().content("message 1").build(),
                        MessageResponse.builder().content("message 2").build()
                )
        );

        mockMvc.perform(get("/api/messages/123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("message 1"))
                .andExpect(jsonPath("$[1].content").value("message 2"));
    }

    @Test
    void testEditMessage_Success() throws Exception {
        MessageResponse mockResponse = MessageResponse.builder()
                .content("Updated text")
                .build();

        Mockito.when(messageService.editMessage(Mockito.eq(10L), any(MessageRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/api/messages/edit/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"group\":false,\"chatId\":999,\"content\":\"Updated text\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated text"));
    }

    @Test
    void testDeleteMessage_Success() throws Exception {
        mockMvc.perform(delete("/api/messages/delete/15")
                .param("isGroup", "false"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(messageService).deleteMessage(eq(15L), eq(false));
    }
}
