package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DirectMessageService {
    DirectMessageEntity saveMessage(DirectMessageRequest messageDto);
    Page<DirectMessageEntity> getMessages(String channelId, Pageable pageable);
}
