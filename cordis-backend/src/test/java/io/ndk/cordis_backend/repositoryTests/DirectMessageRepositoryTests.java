package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.DirectMessageEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.repository.DirectMessageRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class DirectMessageRepositoryTests {

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        directMessageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindByChannelId() {
        Long chatId = 1L;

        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .userName("owner-user")
                .password("password")
                .status(UserStatus.ONLINE)
                .build();

        UserEntity savedUser = userRepository.save(user);

        DirectMessageEntity msg1 = DirectMessageEntity.builder()
                .chatId(chatId)
                .sender(savedUser)
                .content("Message 1")
                .timestamp(LocalDateTime.now().minusDays(2))
                .build();

        DirectMessageEntity msg2 = DirectMessageEntity.builder()
                .chatId(chatId)
                .sender(savedUser)
                .content("Message 2")
                .timestamp(LocalDateTime.now().minusDays(1))
                .build();

        directMessageRepository.save(msg1);
        directMessageRepository.save(msg2);

        Page<DirectMessageEntity> page =
                directMessageRepository.findByChannelId(chatId, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        assertEquals("Message 1", page.getContent().get(0).getContent());
        assertEquals("Message 2", page.getContent().get(1).getContent());
    }

    @Test
    void testFindByChatId() {
        Long chatId = 2L;

        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .userName("owner-user")
                .password("password")
                .status(UserStatus.ONLINE)
                .build();

        UserEntity savedUser = userRepository.save(user);

        DirectMessageEntity msg = DirectMessageEntity.builder()
                .chatId(chatId)
                .sender(savedUser)
                .content("Hello Chat")
                .timestamp(LocalDateTime.now())
                .build();

        directMessageRepository.save(msg);

        List<DirectMessageEntity> messages = directMessageRepository.findByChatId(chatId);
        Assertions.assertFalse(messages.isEmpty());
        assertEquals("Hello Chat", messages.get(0).getContent());
    }

    @Test
    void testFindTopByChatIdOrderByTimestampDesc() {
        Long chatId = 3L;

        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .userName("owner-user")
                .password("password")
                .status(UserStatus.ONLINE)
                .build();

        UserEntity savedUser = userRepository.save(user);

        DirectMessageEntity oldMessage = DirectMessageEntity.builder()
                .chatId(chatId)
                .sender(savedUser)
                .content("Old message")
                .timestamp(LocalDateTime.now().minusDays(3))
                .build();
                
        DirectMessageEntity newMessage = DirectMessageEntity.builder()
                .chatId(chatId)
                .sender(savedUser)
                .content("Newest message")
                .timestamp(LocalDateTime.now())
                .build();

        directMessageRepository.save(oldMessage);
        directMessageRepository.save(newMessage);

        DirectMessageEntity result =
                directMessageRepository.findTopByChatIdOrderByTimestampDesc(chatId);

        Assertions.assertNotNull(result);
        assertEquals("Newest message", result.getContent());
    }
}
