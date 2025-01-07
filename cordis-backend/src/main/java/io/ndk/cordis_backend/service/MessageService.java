package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    MessageResponse saveMessage(MessageRequest messageDto);
//    Page<MessageResponse> getMessages(Long chatId, int page, int size);
    List<MessageResponse> getMessages(Long chatId);
}
