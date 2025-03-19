package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    @MessageMapping("/delete-message")
    @SendTo("/group/public")
    public Map<String, Object> deleteMessage(@Payload Map<String, Object> payload) {
        Long messageId = Long.valueOf(payload.get("messageId").toString());
        Long chatId = Long.valueOf(payload.get("chatId").toString());
        boolean isGroup = Boolean.parseBoolean(payload.get("isGroup").toString());

        String topic = isGroup ? "/group/" + chatId : "/user/" + chatId;
        Map<String, Object> response = new HashMap<>();
        response.put("action", "delete");
        response.put("messageId", messageId);

        template.convertAndSend(topic, response);

        return response;
    }
}
