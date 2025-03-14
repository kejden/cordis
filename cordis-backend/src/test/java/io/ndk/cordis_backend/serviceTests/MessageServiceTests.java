package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.impl.DirectMessageMapper;
import io.ndk.cordis_backend.Mappers.impl.ServerMessageMapper;
import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.repository.ServerMessageRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.entity.ServerMessageEntity;
import io.ndk.cordis_backend.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MessageServiceTests {

    @Mock
    private DirectMessageRepository directMessageRepository;

    @Mock
    private ServerMessageRepository serverMessageRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DirectMessageMapper directMessageMapper;

    @Mock
    private ServerMessageMapper serverMessageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void testSaveMessage_GroupMessage_Success() {
        MessageRequest messageRequest = MessageRequest.builder()
                .group(true)
                .userId(1L)
                .chatId(100L)
                .content("Hello group!")
                .build();

        UserEntity mockUser = UserEntity.builder().id(1L).userName("TestUser").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        ServerMessageEntity savedEntity = ServerMessageEntity.builder()
                .id(10L)
                .content("Hello group!")
                .timestamp(LocalDateTime.now())
                .chatId(100L)
                .sender(mockUser)
                .build();

        when(serverMessageRepository.save(any(ServerMessageEntity.class))).thenReturn(savedEntity);

        MessageResponse mappedResponse = MessageResponse.builder().content("Hello group!").build();
        when(serverMessageMapper.mapTo(savedEntity)).thenReturn(mappedResponse);

        MessageResponse result = messageService.saveMessage(messageRequest);

        assertNotNull(result);
        assertEquals("Hello group!", result.getContent());
        verify(serverMessageRepository, times(1)).save(any(ServerMessageEntity.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/group/" + messageRequest.getChatId()), eq(mappedResponse));
    }

    @Test
    void testSaveMessage_DirectMessage_Success() {
        MessageRequest messageRequest = MessageRequest.builder()
                .group(false)
                .userId(1L)
                .chatId(200L)
                .content("Hello direct!")
                .build();

        UserEntity mockUser = UserEntity.builder().id(1L).userName("DirectUser").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        DirectMessageEntity savedEntity = DirectMessageEntity.builder()
                .id(20L)
                .content("Hello direct!")
                .timestamp(LocalDateTime.now())
                .chatId(200L)
                .sender(mockUser)
                .build();

        when(directMessageRepository.save(any(DirectMessageEntity.class))).thenReturn(savedEntity);

        MessageResponse mappedResponse = MessageResponse.builder().content("Hello direct!").build();
        when(directMessageMapper.mapTo(savedEntity)).thenReturn(mappedResponse);

        MessageResponse result = messageService.saveMessage(messageRequest);

        assertNotNull(result);
        assertEquals("Hello direct!", result.getContent());
        verify(directMessageRepository, times(1)).save(any(DirectMessageEntity.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/user/" + messageRequest.getChatId()), eq(mappedResponse));
    }

    @Test
    void testSaveMessage_NoSuchUser() {
        MessageRequest messageRequest = MessageRequest.builder()
                .group(false)
                .userId(999L)
                .chatId(200L)
                .content("Message from unknown user")
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> messageService.saveMessage(messageRequest)
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
        verify(directMessageRepository, never()).save(any());
        verify(serverMessageRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testGetMessages_Success() {
        Long chatId = 123L;
        DirectMessageEntity dm = DirectMessageEntity.builder()
                .id(55L)
                .content("Direct")
                .chatId(chatId)
                .build();
        when(directMessageRepository.findByChatId(chatId)).thenReturn(List.of(dm));

        MessageResponse mr = MessageResponse.builder().content("Direct").build();
        when(directMessageMapper.mapTo(dm)).thenReturn(mr);

        List<MessageResponse> result = messageService.getMessages(chatId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Direct", result.get(0).getContent());
        verify(directMessageRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void testGetMessages_Empty() {
        Long chatId = 999L;
        when(directMessageRepository.findByChatId(chatId)).thenReturn(Collections.emptyList());

        List<MessageResponse> result = messageService.getMessages(chatId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(directMessageRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void testGetGroupMessages_Success() {
        Long chatId = 300L;
        ServerMessageEntity sme = ServerMessageEntity.builder()
                .id(77L)
                .content("Group HL")
                .chatId(chatId)
                .build();
        when(serverMessageRepository.findByChatId(chatId)).thenReturn(List.of(sme));

        MessageResponse mr = MessageResponse.builder().content("Group").build();
        mr.setContent("Group");
        when(serverMessageMapper.mapTo(sme)).thenReturn(mr);

        List<MessageResponse> result = messageService.getGroupMessages(chatId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Group", result.get(0).getContent());
        verify(serverMessageRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void testGetGroupMessages_Empty() {
        Long chatId = 999L;
        when(serverMessageRepository.findByChatId(chatId)).thenReturn(Collections.emptyList());

        List<MessageResponse> result = messageService.getGroupMessages(chatId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serverMessageRepository, times(1)).findByChatId(chatId);
    }
}
