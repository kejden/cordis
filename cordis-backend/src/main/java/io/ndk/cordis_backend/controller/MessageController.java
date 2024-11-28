package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.DirectMessageRequest;
import io.ndk.cordis_backend.dto.response.DirectMessageResponse;
import io.ndk.cordis_backend.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class DirectMessageController {

    private final DirectMessageService messageService;

    @PostMapping("/create")
    public ResponseEntity<DirectMessageResponse> create(@Valid @RequestBody DirectMessageRequest directMessageRequest) {
        DirectMessageResponse dm = messageService.saveMessage(directMessageRequest);
        return new ResponseEntity<>(dm, HttpStatus.OK);
    }

    // TODO change implementation from List to Page
    @GetMapping("/messages/{channelId}")
    public ResponseEntity<?> getMessages(@PathVariable Long channelId){
        return new ResponseEntity<>(messageService.getMessages(channelId), HttpStatus.OK);
    }
}
