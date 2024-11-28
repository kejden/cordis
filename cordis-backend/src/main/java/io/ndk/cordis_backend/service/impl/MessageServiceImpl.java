package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.dto.response.DirectMessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.service.DirectMessageService;
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
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Mapper<DirectMessageEntity, DirectMessageResponse> mapper;

    @Override
    public DirectMessageResponse saveMessage(DirectMessageRequest messageDto) {
        DirectMessageEntity message = DirectMessageEntity.builder()
                .sender(messageDto.getSender())
                .receiver(messageDto.getReceiver())
                .content(messageDto.getContent())
                .timestamp(LocalDateTime.now())
                .channelId(messageDto.getChannelId())
                .build();
        DirectMessageEntity savedMessage = messageRepository.save(message);
        messagingTemplate.convertAndSend("/user/" + message.getChannelId(), savedMessage);
        DirectMessageResponse messageResponse = new DirectMessageResponse().builder()
                .id(savedMessage.getId())
                .sender(savedMessage.getSender())
                .receiver(savedMessage.getReceiver())
                .content(savedMessage.getContent())
                .channelId(savedMessage.getChannelId())
                .sendAt(savedMessage.getTimestamp())
                .build();
        return messageResponse;
    }

    @Override
    public Page<DirectMessageResponse> getMessages(Long channelId, Pageable pageable) {
        return messageRepository.findByChannelId(channelId, pageable).map(mapper::mapTo);
    }

    @Override
    public List<DirectMessageResponse> getMessages(Long channelId) {
        return messageRepository.findByChannelId(channelId).stream().map(mapper::mapTo).collect(Collectors.toList());
    }


}
