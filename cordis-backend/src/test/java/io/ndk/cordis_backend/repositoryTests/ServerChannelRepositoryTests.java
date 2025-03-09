package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.ServerChannelEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.repository.ServerChannelRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ServerChannelRepositoryTests {

    @Autowired
    private ServerChannelRepository repository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity savedUser;
    private ServerEntity savedServer;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        serverRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(
            UserEntity.builder()
                    .email("test@example.com")
                    .userName("owner-user")
                    .password("password")
                    .status(UserStatus.ONLINE)
                    .build()
        );

        savedServer = serverRepository.save(
            ServerEntity.builder()
                    .name("Server 1")
                    .owner(savedUser)
                    .build()
        );
    }

    @Test
    void testFindByServerId() {
        ServerChannelEntity channel1 = ServerChannelEntity.builder()
                .server(savedServer)
                .name("Channel 1")
                .build();

        ServerChannelEntity channel2 = ServerChannelEntity.builder()
                .server(savedServer)
                .name("Channel 2")
                .build();

        repository.save(channel1);
        repository.save(channel2);

        List<ServerChannelEntity> channels = repository.findByServerId(savedServer.getId());

        assertTrue(channels.size() == 2);
        assertEquals("Channel 1", channels.get(0).getName());
        assertEquals("Channel 2", channels.get(1).getName());
    }

    @Test
    void testFindByServerIdEmpty() {
        List<ServerChannelEntity> channels = repository.findByServerId(savedServer.getId());
        assertEquals(0, channels.size());
    }
}
