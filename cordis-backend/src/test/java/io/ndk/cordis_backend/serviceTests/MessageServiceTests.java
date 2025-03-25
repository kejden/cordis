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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        MessageRequest request = MessageRequest.builder()
                .group(true)
                .userId(1L)
                .chatId(100L)
                .content("Hello group!")
                .build();
        UserEntity user = UserEntity.builder().id(1L).userName("TestUser").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ServerMessageEntity savedEntity = ServerMessageEntity.builder()
                .id(10L)
                .content("Hello group!")
                .timestamp(LocalDateTime.now())
                .chatId(100L)
                .sender(user)
                .build();
        when(serverMessageRepository.save(any(ServerMessageEntity.class))).thenReturn(savedEntity);

        MessageResponse mappedResponse = MessageResponse.builder()
                .content("Hello group!")
                .build();
        when(serverMessageMapper.mapTo(savedEntity)).thenReturn(mappedResponse);

        MessageResponse result = messageService.saveMessage(request);

        assertNotNull(result);
        assertEquals("Hello group!", result.getContent());
        verify(serverMessageRepository, times(1)).save(any(ServerMessageEntity.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/group/100"), eq(mappedResponse));
    }

    @Test
    void testSaveMessage_DirectMessage_Success() {
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .userId(1L)
                .chatId(200L)
                .content("Hello direct!")
                .build();
        UserEntity user = UserEntity.builder().id(1L).userName("DirectUser").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        DirectMessageEntity savedEntity = DirectMessageEntity.builder()
                .id(20L)
                .content("Hello direct!")
                .timestamp(LocalDateTime.now())
                .chatId(200L)
                .sender(user)
                .build();
        when(directMessageRepository.save(any(DirectMessageEntity.class))).thenReturn(savedEntity);

        MessageResponse mappedResponse = MessageResponse.builder()
                .content("Hello direct!")
                .build();
        when(directMessageMapper.mapTo(savedEntity)).thenReturn(mappedResponse);

        MessageResponse result = messageService.saveMessage(request);

        assertNotNull(result);
        assertEquals("Hello direct!", result.getContent());
        verify(directMessageRepository, times(1)).save(any(DirectMessageEntity.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/user/200"), eq(mappedResponse));
    }

    @Test
    void testSaveMessage_NoSuchUser() {
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .userId(999L)
                .chatId(200L)
                .content("Unknown user message")
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> messageService.saveMessage(request)
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
                .content("Message 1")
                .chatId(chatId)
                .build();
        when(directMessageRepository.findByChatId(eq(chatId), any())).thenReturn(List.of(dm));

        MessageResponse mr = MessageResponse.builder().content("Message 1").build();
        when(directMessageMapper.mapTo(dm)).thenReturn(mr);

        List<MessageResponse> result = messageService.getMessages(chatId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Message 1", result.get(0).getContent());
        verify(directMessageRepository, times(1)).findByChatId(eq(chatId), any());
    }

    @Test
    void testGetMessages_Empty() {
        Long chatId = 999L;
        when(directMessageRepository.findByChatId(eq(chatId), any())).thenReturn(Collections.emptyList());

        List<MessageResponse> result = messageService.getMessages(chatId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(directMessageRepository, times(1)).findByChatId(eq(chatId), any());
    }

    @Test
    void testGetGroupMessages_Success() {
        Long chatId = 300L;
        ServerMessageEntity sme = ServerMessageEntity.builder()
                .id(77L)
                .content("Group message 1")
                .chatId(chatId)
                .build();
        when(serverMessageRepository.findByChatId(eq(chatId), any())).thenReturn(List.of(sme));

        MessageResponse mr = MessageResponse.builder().content("Group message 1").build();
        when(serverMessageMapper.mapTo(sme)).thenReturn(mr);

        List<MessageResponse> result = messageService.getGroupMessages(chatId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Group message 1", result.get(0).getContent());
        verify(serverMessageRepository, times(1)).findByChatId(eq(chatId), any());
    }

    @Test
    void testGetGroupMessages_Empty() {
        Long chatId = 999L;
        when(serverMessageRepository.findByChatId(eq(chatId), any())).thenReturn(Collections.emptyList());

        List<MessageResponse> result = messageService.getGroupMessages(chatId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serverMessageRepository, times(1)).findByChatId(eq(chatId), any());
    }

    @Test
    void testEditMessage_GroupMessage_Success() {
        Long messageId = 1L;
        Principal principal = () -> "principalUser@example.com";
        UserEntity sender = UserEntity.builder()
                .id(10L)
                .email("principalUser@example.com")
                .build();

        MessageRequest request = MessageRequest.builder()
                .group(true)
                .content("Edited group content")
                .userId(sender.getId())
                .build();

        ServerMessageEntity original = ServerMessageEntity.builder()
                .id(messageId)
                .chatId(100L)
                .content("Old content")
                .sender(sender)
                .build();
        when(serverMessageRepository.findById(messageId)).thenReturn(Optional.of(original));
        when(userRepository.findByEmail("principalUser@example.com")).thenReturn(Optional.of(sender));

        ServerMessageEntity savedEntity = ServerMessageEntity.builder()
                .id(messageId)
                .chatId(100L)
                .content("Edited group content")
                .build();
        when(serverMessageRepository.save(original)).thenReturn(savedEntity);

        MessageResponse mappedResponse = MessageResponse.builder()
                .content("Edited group content")
                .build();
        when(serverMessageMapper.mapTo(savedEntity)).thenReturn(mappedResponse);

        MessageResponse result = messageService.editMessage(messageId, request, principal);

        assertNotNull(result);
        assertEquals("Edited group content", result.getContent());
        verify(serverMessageRepository, times(1)).findById(messageId);
        verify(serverMessageRepository, times(1)).save(any(ServerMessageEntity.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/group/100"), eq(mappedResponse));
    }

    @Test
    void testEditMessage_DirectMessage_Success() {
        Long messageId = 2L;
        Principal principal = () -> "principalUser@example.com";
        UserEntity sender = UserEntity.builder()
                .id(10L)
                .email("principalUser@example.com")
                .build();

        MessageRequest request = MessageRequest.builder()
                .group(false)
                .content("Edited direct content")
                .userId(sender.getId())
                .build();

        DirectMessageEntity original = DirectMessageEntity.builder()
                .id(messageId)
                .chatId(200L)
                .content("Old direct")
                .sender(sender)
                .build();

        when(userRepository.findByEmail("principalUser@example.com")).thenReturn(Optional.of(sender));

        when(directMessageRepository.findById(messageId)).thenReturn(Optional.of(original));

        DirectMessageEntity savedEntity = DirectMessageEntity.builder()
                .id(messageId)
                .chatId(200L)
                .content("Edited direct content")
                .build();
        when(directMessageRepository.save(original)).thenReturn(savedEntity);

        MessageResponse mappedResponse = MessageResponse.builder()
                .content("Edited direct content")
                .build();
        when(directMessageMapper.mapTo(savedEntity)).thenReturn(mappedResponse);

        MessageResponse result = messageService.editMessage(messageId, request, principal);

        assertNotNull(result);
        assertEquals("Edited direct content", result.getContent());
        verify(directMessageRepository, times(1)).findById(messageId);
        verify(directMessageRepository, times(1)).save(original);
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/user/200"), eq(mappedResponse));
    }

    @Test
    void testEditMessage_NoSuchMessage_Group() {
        Long messageId = 999L;
        Principal principal = () -> "principalUser@example.com";
        MessageRequest request = MessageRequest.builder()
                .group(true)
                .content("Any content")
                .build();

        when(serverMessageRepository.findById(messageId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail("principalUser@example.com")).thenReturn(Optional.of(new UserEntity()));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> messageService.editMessage(messageId, request, principal));
        assertEquals(BusinessErrorCodes.NO_SUCH_MESSAGE, ex.getErrorCode());

        verify(serverMessageRepository, times(1)).findById(messageId);
        verify(serverMessageRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testEditMessage_NoSuchMessage_Direct() {
        Long messageId = 888L;
        Principal principal = () -> "principalUser@example.com";
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .content("Any content")
                .build();

        when(directMessageRepository.findById(messageId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail("principalUser@example.com")).thenReturn(Optional.of(new UserEntity()));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> messageService.editMessage(messageId, request, principal));
        assertEquals(BusinessErrorCodes.NO_SUCH_MESSAGE, ex.getErrorCode());

        verify(directMessageRepository, times(1)).findById(messageId);
        verify(directMessageRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testDeleteMessage_GroupMessage_Success() {
        Long messageId = 33L;
        Principal principal = () -> "principalUser@example.com";
        UserEntity sender = UserEntity.builder()
                .id(10L)
                .email("principalUser@example.com")
                .build();
        ServerMessageEntity entity = ServerMessageEntity.builder()
                .id(messageId)
                .chatId(777L)
                .content("Will be deleted")
                .sender(sender)
                .build();

        when(serverMessageRepository.findById(messageId)).thenReturn(Optional.of(entity));
        when(userRepository.findByEmail("principalUser@example.com")).thenReturn(Optional.of(sender));

        messageService.deleteMessage(messageId, true, principal);

        verify(serverMessageRepository, times(1)).delete(entity);
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/group/777"), eq(Map.of("action", "delete", "messageId", messageId)));
    }

    @Test
    void testDeleteMessage_DirectMessage_Success() {
        Long messageId = 44L;
        Principal principal = () -> "principalUser@example.com";
        UserEntity sender = UserEntity.builder()
                .id(10L)
                .email("principalUser@example.com")
                .build();

        DirectMessageEntity entity = DirectMessageEntity.builder()
                .id(messageId)
                .chatId(999L)
                .sender(sender)
                .content("Will be deleted direct")
                .build();

        when(directMessageRepository.findById(messageId)).thenReturn(Optional.of(entity));
        when(userRepository.findByEmail("principalUser@example.com"))
                .thenReturn(Optional.of(sender));

        messageService.deleteMessage(messageId, false, principal);

        verify(directMessageRepository, times(1)).delete(entity);
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/user/999"), eq(Map.of("action", "delete", "messageId", messageId)));
    }

    @Test
    void testDeleteMessage_NoSuchMessage_Group() {
        Long messageId = 500L;
        Principal principal = () -> "principalUser@example.com";

        when(serverMessageRepository.findById(messageId)).thenReturn(Optional.empty());

        when(userRepository.findByEmail("principalUser@example.com"))
                .thenReturn(Optional.of(new UserEntity()));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> messageService.deleteMessage(messageId, true, principal)
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_MESSAGE, ex.getErrorCode());

        verify(serverMessageRepository, times(1)).findById(messageId);
        verify(serverMessageRepository, never()).delete(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testDeleteMessage_NoSuchMessage_Direct() {
        Long messageId = 501L;
        Principal principal = () -> "principalUser@example.com";

        when(directMessageRepository.findById(messageId)).thenReturn(Optional.empty());

        when(userRepository.findByEmail("principalUser@example.com"))
                .thenReturn(Optional.of(new UserEntity()));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> messageService.deleteMessage(messageId, false, principal)
        );
        assertEquals(BusinessErrorCodes.NO_SUCH_MESSAGE, ex.getErrorCode());

        verify(directMessageRepository, times(1)).findById(messageId);
        verify(directMessageRepository, never()).delete(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testEditMessage_DirectMessage_UserIsNotSender_ThrowsNoPermission() {
        // GIVEN
        Long messageId = 1L;
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .content("Trying to edit someone else's DM")
                .build();

        UserEntity realSender = UserEntity.builder()
                .id(10L)
                .email("actualSender@example.com")
                .build();
        DirectMessageEntity existingMessage = DirectMessageEntity.builder()
                .id(messageId)
                .sender(realSender)
                .content("Original DM content")
                .build();

        when(directMessageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));

        Principal principal = () -> "principalUser@example.com";
        UserEntity principalUserEntity = UserEntity.builder()
                .id(20L)
                .email("principalUser@example.com")
                .build();
        when(userRepository.findByEmail("principalUser@example.com"))
                .thenReturn(Optional.of(principalUserEntity));

        CustomException ex = assertThrows(CustomException.class, () ->
                messageService.editMessage(messageId, request, principal)
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
        verify(directMessageRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testEditMessage_DirectMessage_UserIsSender_Success() {
        Long messageId = 2L;
        MessageRequest request = MessageRequest.builder()
                .group(false)
                .content("Updated DM content")
                .build();

        UserEntity realSender = UserEntity.builder()
                .id(1L)
                .email("principalUser@example.com")
                .build();
        DirectMessageEntity existingMessage = DirectMessageEntity.builder()
                .id(messageId)
                .sender(realSender)
                .content("Original DM content")
                .build();

        when(directMessageRepository.findById(messageId))
                .thenReturn(Optional.of(existingMessage));

        Principal principal = () -> "principalUser@example.com";
        when(userRepository.findByEmail("principalUser@example.com"))
                .thenReturn(Optional.of(realSender));

        DirectMessageEntity savedMessage = DirectMessageEntity.builder()
                .id(messageId)
                .sender(realSender)
                .content("Updated DM content")
                .build();
        when(directMessageRepository.save(existingMessage)).thenReturn(savedMessage);

        MessageResponse mappedResponse = MessageResponse.builder()
                .content("Updated DM content")
                .build();
        when(directMessageMapper.mapTo(savedMessage)).thenReturn(mappedResponse);

        MessageResponse result = messageService.editMessage(messageId, request, principal);

        assertNotNull(result);
        assertEquals("Updated DM content", result.getContent());
        verify(directMessageRepository).save(existingMessage);
        verify(messagingTemplate).convertAndSend(eq("/user/" + savedMessage.getChatId()), eq(mappedResponse));
    }

    @Test
    void testDeleteMessage_DirectMessage_UserIsNotSender_ThrowsNoPermission() {
        Long messageId = 5L;
        boolean isGroup = false;
        UserEntity realSender = UserEntity.builder()
                .id(10L)
                .email("somebodyElse@example.com")
                .build();
        DirectMessageEntity existingMessage = DirectMessageEntity.builder()
                .id(messageId)
                .sender(realSender)
                .build();
        when(directMessageRepository.findById(messageId))
                .thenReturn(Optional.of(existingMessage));

        Principal principal = () -> "principalUser@example.com";
        UserEntity principalUser = UserEntity.builder()
                .id(20L)
                .email("principalUser@example.com")
                .build();
        when(userRepository.findByEmail("principalUser@example.com"))
                .thenReturn(Optional.of(principalUser));

        CustomException ex = assertThrows(CustomException.class, () ->
                messageService.deleteMessage(messageId, isGroup, principal)
        );
        assertEquals(BusinessErrorCodes.NO_PERMISSION, ex.getErrorCode());
        verify(directMessageRepository, never()).delete(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }
}
