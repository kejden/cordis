package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final DirectMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final Mapper<DirectMessageEntity, MessageResponse> mapper;

    @Override
    public MessageResponse saveMessage(MessageRequest messageDto) {
        DirectMessageEntity message = DirectMessageEntity.builder()
                .content(messageDto.getContent())
                .sender(getUser(messageDto.getUserId()))
                .timestamp(LocalDateTime.now())
                .chatId(messageDto.getChatId())
                .build();
        DirectMessageEntity savedMessage = messageRepository.save(message);
        MessageResponse messageResponse = mapper.mapTo(savedMessage);
        messagingTemplate.convertAndSend("/user/" + message.getChatId(), messageResponse);
        return messageResponse;
    }

    @Override
    public Page<MessageResponse> getMessages(Long channelId, Pageable pageable) {
        return messageRepository.findByChannelId(channelId, pageable).map(mapper::mapTo);
    }

    @Override
    public List<MessageResponse> getMessages(Long chatId) {
        return messageRepository.findByChatId(chatId).stream().map(mapper::mapTo).collect(Collectors.toList());
    }

    private UserEntity getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
    }

}
