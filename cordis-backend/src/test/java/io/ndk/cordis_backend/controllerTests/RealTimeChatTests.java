package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.controller.RealTImeChat;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = RealTImeChat.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RealTimeChatTests {

    @MockBean
    private SimpMessagingTemplate template;

    @InjectMocks
    private RealTImeChat realTImeChat;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private io.ndk.cordis_backend.service.impl.customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CookieService cookieService;

    private DirectMessageEntity message;
    private Map<String, Object> payload;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        realTImeChat = new RealTImeChat(template);
        message = new DirectMessageEntity();
        message.setChatId(1L);
        message.setContent("Hello, World!");

        payload = new HashMap<>();
        payload.put("messageId", 123L);
        payload.put("chatId", 1L);
        payload.put("isGroup", true);
    }

    @Test
    public void testReceiveMessage() {
        DirectMessageEntity message = new DirectMessageEntity();
        message.setChatId(123L);
        DirectMessageEntity returnedMessage = realTImeChat.receiveMessage(message);
        verify(template, times(1)).convertAndSend("/group123", message);
        assertEquals(message, returnedMessage);
    }

    @Test
    public void testEditMessage() {
        DirectMessageEntity message = new DirectMessageEntity();
        message.setChatId(456L);
        DirectMessageEntity returnedMessage = realTImeChat.editMessage(message);
        verify(template, times(1)).convertAndSend("/group456", message);
        assertEquals(message, returnedMessage);
    }

    @Test
    public void testDeleteMessageGroup() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageId", 10L);
        payload.put("chatId", 999L);
        payload.put("isGroup", true);

        Map<String, Object> response = realTImeChat.deleteMessage(payload);

        verify(template, times(1)).convertAndSend("/group/999", response);
        assertEquals("delete", response.get("action"));
        assertEquals(10L, response.get("messageId"));
    }

    @Test
    public void testDeleteMessageDM() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageId", 10L);
        payload.put("chatId", 999L);
        payload.put("isGroup", false);

        Map<String, Object> response = realTImeChat.deleteMessage(payload);

        verify(template, times(1)).convertAndSend("/user/999", response);
        assertEquals("delete", response.get("action"));
        assertEquals(10L, response.get("messageId"));
    }
}
