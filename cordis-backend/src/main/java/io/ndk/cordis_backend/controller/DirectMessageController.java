package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.config.notification.DirectMessageNotification;
import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.dto.response.DirectMessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class DirectMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DirectMessageService messageService;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload DirectMessageRequest dmRequest
    ){
        DirectMessageResponse dmEntity = messageService.saveMessage(dmRequest);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(dmEntity.getChannelId()), "queue/messages", DirectMessageNotification.builder()
                                .id(dmEntity.getId())
                                .sender(dmEntity.getSender())
                                .receiver(dmEntity.getReceiver())
                                .content(dmEntity.getContent())
                        .build()
        );
    }

    // TODO change implementation from List to Page
    @GetMapping("/messages/{channelId}")
    public ResponseEntity<?> getMessages(@PathVariable Long channelId){
        List<DirectMessageEntity> messages = messageService.getMessages(channelId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

}
