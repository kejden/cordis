package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.dto.response.DirectMessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectMessageService {
    DirectMessageResponse saveMessage(DirectMessageRequest messageDto);
    Page<DirectMessageEntity> getMessages(Long channelId, Pageable pageable);
    List<DirectMessageEntity> getMessages(Long channelId);
}
