package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class DirectMessageController {

    private final DirectMessageService messageService;

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<Page<DirectMessageEntity>> getPaginatedMessages(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        Page<DirectMessageEntity> messages = messageService.getMessages(channelId, pageable);
        return ResponseEntity.ok(messages);
    }

}
