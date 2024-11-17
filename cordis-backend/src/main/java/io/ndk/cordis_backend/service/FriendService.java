package io.ndk.cordis_backend.service;

import io.ndk.cordis_backend.dto.request.FriendRequest;
import io.ndk.cordis_backend.dto.response.FriendResponse;

import java.util.List;

public interface FriendService {
    void requestFriend(FriendRequest friendRequest, String email);
    List<FriendResponse> getFriendResponse(String email);
    List<FriendResponse> getPendingFriendResponse(String name);
    List<FriendResponse> getAwaitingFriendResponse(String name);
    void refuseFriend(Long id);
    void addFriend(Long id);
    void banFriend(Long id);
}
