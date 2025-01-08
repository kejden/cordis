package io.ndk.cordis_backend.controller;

import io.ndk.cordis_backend.dto.request.FriendRequest;
import io.ndk.cordis_backend.dto.response.FriendResponse;
import io.ndk.cordis_backend.service.FriendService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/friend")
@AllArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<Void> requestFriend(@RequestBody FriendRequest friendRequest, Principal principal) {
        friendService.requestFriend(friendRequest, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/responses")
    public ResponseEntity<List<FriendResponse>> getFriendResponses(Principal principal) {
        List<FriendResponse> friendResponses = friendService.getFriendResponse(principal.getName());
        return new ResponseEntity<>(friendResponses, HttpStatus.OK);
    }
    // do kogos
    @GetMapping("/pending")
    public ResponseEntity<List<FriendResponse>> getPendingFriendRequests(Principal principal) {
        List<FriendResponse> pendingResponses = friendService.getPendingFriendResponse(principal.getName());
        return ResponseEntity.ok(pendingResponses);
    }
    // uzytkownika
    @GetMapping("/awaiting")
    public ResponseEntity<List<FriendResponse>> getAwaitingFriendRequests(Principal principal) {
        List<FriendResponse> awaitingResponses = friendService.getAwaitingFriendResponse(principal.getName());
        return ResponseEntity.ok(awaitingResponses);
    }

    @DeleteMapping("/refuse/{id}")
    public ResponseEntity<Void> refuseFriend(@PathVariable Long id) {
        friendService.refuseFriend(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id) {
        friendService.addFriend(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/ban/{id}")
    public ResponseEntity<Void> banFriend(@PathVariable Long id) {
        friendService.banFriend(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/latestChats")
    public ResponseEntity<?> latestChats(Principal principal) {
        return new ResponseEntity<>( friendService.latestChats(principal.getName()), HttpStatus.OK);
    }
}
