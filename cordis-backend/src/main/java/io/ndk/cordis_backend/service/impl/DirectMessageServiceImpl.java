package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository messageRepository;


    @Override
    public DirectMessageEntity saveMessage(DirectMessageRequest messageDto) {
        DirectMessageEntity message = DirectMessageEntity.builder()
                .sender(messageDto.getSender())
                .content(messageDto.getContent())
                .timestamp(LocalDateTime.now())
                .channelId(messageDto.getChannelId())
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Page<DirectMessageEntity> getMessages(Long channelId, Pageable pageable) {
        return messageRepository.findByChannelId(channelId, pageable);
    }
}
