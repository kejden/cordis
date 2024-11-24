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

    private String generateChannelId(Long user1, Long user2) {
        String u1 = user1.toString();
        String u2 = user2.toString();

        return u1.compareTo(u2) < 0 ? u1 + "_" + u2 : u2 + "_" + u1;
    }

    @Override
    public DirectMessageEntity saveMessage(DirectMessageRequest messageDto) {
        String channelId = generateChannelId(messageDto.getSenderId(), messageDto.getReceiverId());

        DirectMessageEntity message = DirectMessageEntity.builder()
                .senderId(messageDto.getSenderId())
                .receiverId(messageDto.getReceiverId())
                .content(messageDto.getContent())
                .timestamp(LocalDateTime.now())
                .channelId(channelId)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Page<DirectMessageEntity> getMessages(String channelId, Pageable pageable) {
        return messageRepository.findByChannelId(channelId, pageable);
    }
}
