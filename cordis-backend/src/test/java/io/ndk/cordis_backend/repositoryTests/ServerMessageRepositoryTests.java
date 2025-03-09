package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.ServerMessageEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.repository.ServerMessageRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ServerMessageRepositoryTests {

    @Autowired
    private ServerMessageRepository serverMessageRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity savedUser;

    @BeforeEach
    void setUp() {
        serverMessageRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(UserEntity.builder()
                .id(0L)
                .email("email@email.com")
                .password("password")
                .userName("username")
                .status(UserStatus.OFFLINE)
                .profileImage("profile_image.png")
                .build());
    }

    @Test
    void testFindByChatId() {
        ServerMessageEntity message1 = ServerMessageEntity.builder()
                .chatId(1L)
                .content("Test 1")
                .sender(savedUser)
                .timestamp(LocalDateTime.now().minusDays(1))
                .build();

        ServerMessageEntity message2 = ServerMessageEntity.builder()
                .chatId(1L)
                .content("Test 2")
                .sender(savedUser)
                .timestamp(LocalDateTime.now().minusDays(1))
                .build();

        ServerMessageEntity message3 = ServerMessageEntity.builder()
                .chatId(2L)
                .content("Test 3")
                .sender(savedUser)
                .timestamp(LocalDateTime.now().minusDays(1))
                .build();


        serverMessageRepository.save(message1);
        serverMessageRepository.save(message2);
        serverMessageRepository.save(message3);

        List<ServerMessageEntity> results = serverMessageRepository.findByChatId(1L);
        List<ServerMessageEntity> results2 = serverMessageRepository.findByChatId(2L);

        assertEquals(2, results.size());
        assertEquals(1, results2.size());
    }

    @Test
    void testFindByChatIdEmpty() {
        List<ServerMessageEntity> results = serverMessageRepository.findByChatId(1L);

        assertEquals(0, results.size());
    }

}
