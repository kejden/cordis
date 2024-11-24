package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class DirectMessageWebSocketController {
    private final DirectMessageService messageService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{channelId}") 
    public DirectMessageEntity sendMessage(@Payload DirectMessageRequest messageDto) {
        DirectMessageEntity savedMessage = messageService.saveMessage(messageDto);
        return savedMessage;
    }
}
