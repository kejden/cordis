package io.ndk.cordis_backend.service.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.FriendRequest;
import io.ndk.cordis_backend.dto.response.FriendResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.entity.FriendEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.FriendState;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.repository.FriendRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.FriendService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final DirectMessageRepository directMessageRepository;
    private final Mapper<FriendEntity, FriendResponse> mapper;
    private final Mapper<UserEntity, UserDto> userMapper;

    @Override
    public void requestFriend(FriendRequest friendRequest, String email) {
        UserEntity sender = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        UserEntity receiver = userRepository.findByUserName(friendRequest.getUserName()).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        FriendEntity friendEntity = new FriendEntity().builder()
                .sender(sender)
                .receiver(receiver)
                .friendState(FriendState.REQUEST)
                .build();
        friendRepository.save(friendEntity);
    }

    @Override
    public List<FriendResponse> getFriendResponse(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));

        List<FriendEntity> responseList = user.getResponseList();
        List<FriendEntity> requestList = user.getRequestList();

        List<FriendResponse> result = responseList.stream()
                .filter(f -> f.getFriendState().equals(FriendState.ACCEPT))
                .map(this::convertRequestFriend)
                .collect(Collectors.toList());

        List<FriendResponse> send = requestList.stream()
                .filter(f -> f.getFriendState().equals(FriendState.ACCEPT))
                .map(this::convertResponseFriend)
                .collect(Collectors.toList());
        result.addAll(send);
        return result;
    }

    @Override
    public List<FriendResponse> getPendingFriendResponse(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));

        List<FriendEntity> requestList = user.getRequestList();
        return requestList.stream()
                .filter(f -> f.getFriendState().equals(FriendState.REQUEST))
                .map(this::convertResponseFriend)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendResponse> getAwaitingFriendResponse(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));

        List<FriendEntity> responseList = user.getResponseList();
        return responseList.stream()
                .filter(f -> f.getFriendState().equals(FriendState.REQUEST))
                .map(this::convertRequestFriend)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendResponse> latestChats(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));

        List<FriendEntity> responseList = user.getResponseList();
        List<FriendEntity> requestList = user.getRequestList();

        List<FriendResponse> result = responseList.stream()
                .filter(f -> f.getFriendState().equals(FriendState.ACCEPT))
                .map(this::convertRequestFriend)
                .collect(Collectors.toList());

        List<FriendResponse> send = requestList.stream()
                .filter(f -> f.getFriendState().equals(FriendState.ACCEPT))
                .map(this::convertResponseFriend)
                .collect(Collectors.toList());
        result.addAll(send);

        List<FriendResponse> sortedFriends = result.stream()
                .sorted(Comparator.comparing((FriendResponse friend) -> {
                    DirectMessageEntity latestMessage = directMessageRepository.findTopByChatIdOrderByTimestampDesc(friend.getId());
                    return latestMessage != null ? latestMessage.getTimestamp() : LocalDateTime.MIN;
                }).reversed())
                .collect(Collectors.toList());

        return sortedFriends;
    }

    @Override
    public void refuseFriend(Long id) {
        FriendEntity friendEntity = friendRepository.findById(id).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_ID));
        friendRepository.delete(friendEntity);
    }

    @Override
    public void addFriend(Long id) {
        FriendEntity friendEntity = friendRepository.findById(id).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_ID));
        friendEntity.setFriendState(FriendState.ACCEPT);
        friendRepository.save(friendEntity);
    }

    @Override
    public void banFriend(Long id) {
        FriendEntity friendEntity = friendRepository.findById(id).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_ID));
        friendEntity.setFriendState(FriendState.BAN);
        friendRepository.save(friendEntity);
    }

    private FriendResponse convertRequestFriend(FriendEntity friend) {
        FriendResponse response = FriendResponse.builder()
                .id(friend.getId())
                .user(userMapper.mapTo(fetchUserByUsername(friend.getSender().getUserName())))
                .state(friend.getFriendState().toString().equals("REQUEST") ? "WAITING" : friend.getFriendState().toString()).build();

        return response;
    }

    private FriendResponse convertResponseFriend(FriendEntity friend) {
        FriendResponse response = FriendResponse.builder()
                .id(friend.getId())
                .user(userMapper.mapTo(fetchUserByUsername(friend.getReceiver().getUserName())))
                .state(friend.getFriendState().toString()).build();

        return response;
    }

    private UserEntity fetchUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow(
                () -> new CustomException(BusinessErrorCodes.NO_USERNAME)
        );
    }
}
