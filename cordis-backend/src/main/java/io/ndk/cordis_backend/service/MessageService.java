package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

public interface MessageService {
    MessageResponse saveMessage(MessageRequest messageDto);
    List<MessageResponse> getMessages(Long chatId);
    List<MessageResponse> getGroupMessages(Long chatId);
    MessageResponse editMessage(Long messageId, MessageRequest newMessage, Principal principal);
    void deleteMessage(Long id, boolean isGroup, Principal principal);
}
