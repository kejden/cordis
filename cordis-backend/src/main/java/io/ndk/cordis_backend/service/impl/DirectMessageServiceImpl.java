package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.dto.response.DirectMessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository messageRepository;


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
    public Page<DirectMessageEntity> getMessages(Long channelId, Pageable pageable) {
        return messageRepository.findByChannelId(channelId, pageable);
    }

    @Override
    public List<DirectMessageEntity> getMessages(Long channelId) {
        return messageRepository.findByChannelId(channelId);
    }


}
