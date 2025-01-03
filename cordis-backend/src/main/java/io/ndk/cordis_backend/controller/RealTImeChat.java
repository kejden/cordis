package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class RealTImeChat {

    private SimpMessagingTemplate template;

    @MessageMapping("/message")
    @SendTo("/group/public")
    public DirectMessageEntity receiveMessage(@Payload DirectMessageEntity message) {
        template.convertAndSend("/group"+message.getChatId(), message);
        return message;
    }
}
