package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RealTImeChat {

    private final SimpMessagingTemplate template;

    public RealTImeChat(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/message")
    @SendTo("/group/public")
    public DirectMessageEntity receiveMessage(@Payload DirectMessageEntity message) {
        template.convertAndSend("/group" + message.getChatId(), message);
        return message;
    }

    @MessageMapping("/edit-message")
    @SendTo("/group/public")
    public DirectMessageEntity editMessage(@Payload DirectMessageEntity message) {
        template.convertAndSend("/group" + message.getChatId(), message);
        return message;
    }
}
