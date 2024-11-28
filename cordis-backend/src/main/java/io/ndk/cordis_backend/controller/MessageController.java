package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.MessageRequest;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody MessageRequest directMessageRequest) {
        MessageResponse dm = messageService.saveMessage(directMessageRequest);
        return new ResponseEntity<>(dm, HttpStatus.OK);
    }

    // TODO change implementation from List to Page
    @GetMapping("/messages/{channelId}")
    public ResponseEntity<?> getMessages(@PathVariable Long channelId){
        return new ResponseEntity<>(messageService.getMessages(channelId), HttpStatus.OK);
    }
}
