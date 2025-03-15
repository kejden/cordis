package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.impl.FriendMapperImpl;
import io.ndk.cordis_backend.Mappers.impl.UserDtoMapper;
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
import io.ndk.cordis_backend.service.impl.FriendServiceImpl;
import io.ndk.cordis_backend.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FriendServiceTests {

    @Mock
    private FriendRepository friendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DirectMessageRepository directMessageRepository;
    @Mock
    private FriendMapperImpl mapper;
    @Mock
    private UserDtoMapper userMapper;

    @InjectMocks
    private FriendServiceImpl friendService;

    private static final String TEST_EMAIL = "test@example.com";
    private UserEntity sender;
    private UserEntity receiver;

    @BeforeEach
    void setup() {
        sender = UserEntity.builder().id(1L).email(TEST_EMAIL).userName("senderUser").build();
        receiver = UserEntity.builder().id(2L).userName("receiverUser").build();
    }

    @Test
    void requestFriend_Success() {
        FriendRequest request = FriendRequest.builder().userName("receiverUser").build();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("receiverUser")).thenReturn(Optional.of(receiver));

        friendService.requestFriend(request, TEST_EMAIL);

        verify(friendRepository).save(any(FriendEntity.class));
    }

    @Test
    void requestFriend_NoSuchEmailForSender() {
        FriendRequest request = FriendRequest.builder().userName("receiverUser").build();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> friendService.requestFriend(request, TEST_EMAIL));

        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
        verify(friendRepository, never()).save(any());
    }

    @Test
    void requestFriend_NoSuchEmailForReceiver() {
        FriendRequest request = FriendRequest.builder().userName("unknown").build();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> friendService.requestFriend(request, TEST_EMAIL));

        assertEquals(BusinessErrorCodes.NO_SUCH_EMAIL, ex.getErrorCode());
        verify(friendRepository, never()).save(any());
    }


    @Test
    void getFriendResponse_NoSuchEmail() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> friendService.getFriendResponse(TEST_EMAIL));
    }

    @Test
    void getPendingFriendResponse_Success() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));

        FriendEntity requestEntity = FriendEntity.builder()
                .friendState(FriendState.REQUEST)
                .receiver(receiver)
                .build();
        sender.setRequestList(Collections.singletonList(requestEntity));

        when(userRepository.findByUserName(receiver.getUserName())).thenReturn(Optional.of(receiver));

        when(userMapper.mapTo(receiver)).thenReturn(new UserDto());

        List<FriendResponse> list = friendService.getPendingFriendResponse(TEST_EMAIL);

        assertEquals(1, list.size());
    }

    @Test
    void getAwaitingFriendResponse_Success() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));

        FriendEntity awaiting = FriendEntity.builder()
                .friendState(FriendState.REQUEST)
                .sender(sender)
                .receiver(receiver)
                .build();

        sender.setResponseList(Collections.singletonList(awaiting));

        when(userRepository.findByUserName(sender.getUserName())).thenReturn(Optional.of(sender));
        when(userMapper.mapTo(sender)).thenReturn(new UserDto());

        List<FriendResponse> list = friendService.getAwaitingFriendResponse(TEST_EMAIL);

        assertEquals(1, list.size());
    }

    @Test
    void refuseFriend_Success() {
        when(friendRepository.findById(99L)).thenReturn(Optional.of(FriendEntity.builder().id(99L).build()));
        friendService.refuseFriend(99L);
        verify(friendRepository).delete(any(FriendEntity.class));
    }

    @Test
    void addFriend_Success() {
        FriendEntity friend = FriendEntity.builder().id(101L).friendState(FriendState.REQUEST).build();
        when(friendRepository.findById(101L)).thenReturn(Optional.of(friend));
        friendService.addFriend(101L);
        assertEquals(FriendState.ACCEPT, friend.getFriendState());
        verify(friendRepository).save(friend);
    }

    @Test
    void banFriend_Success() {
        FriendEntity friend = FriendEntity.builder().id(202L).friendState(FriendState.ACCEPT).build();
        when(friendRepository.findById(202L)).thenReturn(Optional.of(friend));
        friendService.banFriend(202L);
        assertEquals(FriendState.BAN, friend.getFriendState());
        verify(friendRepository).save(friend);
    }

    @Test
    void friendMethods_NoSuchId_ThrowsCustomException() {
        when(friendRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> friendService.refuseFriend(999L));
        assertThrows(CustomException.class, () -> friendService.addFriend(999L));
        assertThrows(CustomException.class, () -> friendService.banFriend(999L));
    }

    @Test
    void getFriendResponse_Success_NoFriends() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));
        sender.setResponseList(Collections.emptyList());
        sender.setRequestList(Collections.emptyList());

        List<FriendResponse> result = friendService.getFriendResponse(TEST_EMAIL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userMapper, never()).mapTo(any());
    }

    @Test
    void getFriendResponse_Success_HasAcceptedFriends() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));

        FriendEntity friendInResponse = FriendEntity.builder()
                .id(11L)
                .friendState(FriendState.ACCEPT)
                .sender(UserEntity.builder().id(999L).userName("responseFriend").build())
                .receiver(sender)
                .build();
        FriendEntity friendInRequest = FriendEntity.builder()
                .id(12L)
                .friendState(FriendState.ACCEPT)
                .sender(sender)
                .receiver(UserEntity.builder().id(888L).userName("requestFriend").build())
                .build();

        sender.setResponseList(Collections.singletonList(friendInResponse));
        sender.setRequestList(Collections.singletonList(friendInRequest));

        when(userRepository.findByUserName("responseFriend"))
                .thenReturn(Optional.of(friendInResponse.getSender()));
        when(userRepository.findByUserName("requestFriend"))
                .thenReturn(Optional.of(friendInRequest.getReceiver()));

        when(userMapper.mapTo(friendInResponse.getSender())).thenReturn(new UserDto());
        when(userMapper.mapTo(friendInRequest.getReceiver())).thenReturn(new UserDto());

        List<FriendResponse> result = friendService.getFriendResponse(TEST_EMAIL);

        assertEquals(2, result.size());
    }

    @Test
    void latestChats_Success_NoAcceptedFriends() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));

        FriendEntity friend = FriendEntity.builder()
                .friendState(FriendState.REQUEST)
                .sender(sender)
                .receiver(receiver)
                .build();
        sender.setResponseList(Collections.singletonList(friend));
        sender.setRequestList(Collections.emptyList());

        List<FriendResponse> result = friendService.latestChats(TEST_EMAIL);

        assertTrue(result.isEmpty());
        verify(directMessageRepository, never()).findTopByChatIdOrderByTimestampDesc(anyLong());
    }

    @Test
    void latestChats_Success_SingleAcceptedFriend() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));

        FriendEntity acceptedFriend = FriendEntity.builder()
                .id(999L)
                .friendState(FriendState.ACCEPT)
                .sender(sender)
                .receiver(receiver)
                .build();
        sender.setRequestList(Collections.singletonList(acceptedFriend));
        sender.setResponseList(Collections.emptyList());

        when(userRepository.findByUserName("receiverUser")).thenReturn(Optional.of(receiver));

        List<FriendResponse> result = friendService.latestChats(TEST_EMAIL);

        assertEquals(1, result.size());
        assertEquals(999L, result.get(0).getId());
    }

    @Test
    void latestChats_Success_MultipleAcceptedAndCheckSortOrder() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(sender));

        FriendEntity friendA = FriendEntity.builder()
                .id(300L)
                .friendState(FriendState.ACCEPT)
                .sender(sender)
                .receiver(UserEntity.builder().userName("friendA").build())
                .build();
        FriendEntity friendB = FriendEntity.builder()
                .id(400L)
                .friendState(FriendState.ACCEPT)
                .sender(sender)
                .receiver(UserEntity.builder().userName("friendB").build())
                .build();
        sender.setRequestList(Arrays.asList(friendA, friendB));
        sender.setResponseList(Collections.emptyList());

        DirectMessageEntity lastMsgA = DirectMessageEntity.builder()
                .chatId(300L)
                .timestamp(LocalDateTime.now().minusHours(5))
                .build();
        DirectMessageEntity lastMsgB = DirectMessageEntity.builder()
                .chatId(400L)
                .timestamp(LocalDateTime.now().minusHours(1))
                .build();

        when(directMessageRepository.findTopByChatIdOrderByTimestampDesc(300L)).thenReturn(lastMsgA);
        when(directMessageRepository.findTopByChatIdOrderByTimestampDesc(400L)).thenReturn(lastMsgB);

        when(userRepository.findByUserName("friendA")).thenReturn(Optional.of(friendA.getReceiver()));
        when(userRepository.findByUserName("friendB")).thenReturn(Optional.of(friendB.getReceiver()));
        when(userMapper.mapTo(friendA.getReceiver())).thenReturn(new UserDto());
        when(userMapper.mapTo(friendB.getReceiver())).thenReturn(new UserDto());

        List<FriendResponse> result = friendService.latestChats(TEST_EMAIL);

        assertEquals(2, result.size());
        assertEquals(400L, result.get(0).getId());
        assertEquals(300L, result.get(1).getId());
    }
}
