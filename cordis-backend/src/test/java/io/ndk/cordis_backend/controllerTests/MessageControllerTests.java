package io.ndk.cordis_backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ndk.cordis_backend.controller.MessageController;
import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.MessageService;
import io.ndk.cordis_backend.service.impl.customUserDetailService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import({})
@ActiveProfiles("test")
public class MessageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Principal principal;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        principal = () -> "test@mock.com";
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateMessage_Success() throws Exception {
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .userId(1L)
                .chatId(123L)
                .content("Hello!")
                .build();

        MessageResponse mockResponse = MessageResponse.builder()
                .content("Hello!")
                .build();

        when(messageService.saveMessage(any(MessageRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/messages/create")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello!"));
    }

    @Test
    void testGetMessages_Success() throws Exception {
        when(messageService.getMessages(123L)).thenReturn(
                java.util.List.of(
                    MessageResponse.builder().content("message 1").build(),
                    MessageResponse.builder().content("message 2").build()
                )
        );

        mockMvc.perform(get("/api/messages/123").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("message 1"))
                .andExpect(jsonPath("$[1].content").value("message 2"));
    }

    @Test
    void testEditMessage_Success() throws Exception {
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .chatId(999L)
                .content("Updated text")
                .build();

        MessageResponse mockResponse = MessageResponse.builder()
                .content("Updated text")
                .build();

        when(messageService.editMessage(eq(10L), any(MessageRequest.class), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/api/messages/edit/10")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated text"));
    }

    @Test
    void testEditMessage_ForbiddenWhenNotSender() throws Exception {
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .chatId(10L)
                .content("Updated text")
                .userId(10L)
                .build();

        when(messageService.editMessage(eq(10L), any(MessageRequest.class), any(Principal.class)))
                .thenThrow(new CustomException(BusinessErrorCodes.NO_PERMISSION));

        mockMvc.perform(put("/api/messages/edit/10")
                    .principal(principal)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteMessage_Success() throws Exception {
        mockMvc.perform(delete("/api/messages/delete/15")
                .principal(principal)
                .param("isGroup", "false"))
                .andExpect(status().isNoContent());

        verify(messageService).deleteMessage(eq(15L), eq(false), any());
    }

    @Test
    void testDeleteMessage_ForbiddenWhenNotSender() throws Exception {
        doThrow(new CustomException(BusinessErrorCodes.NO_PERMISSION))
                .when(messageService).deleteMessage(eq(15L), eq(false), any());

        mockMvc.perform(delete("/api/messages/delete/15")
                .principal(principal)
                .param("isGroup", "false"))
                .andExpect(status().isForbidden());
    }
}
