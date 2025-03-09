package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.ServerChannelEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.repository.ServerChannelRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
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

    @Test
    void testFindByServerId(){
        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .userName("owner-user")
                .password("password")
                .status(UserStatus.ONLINE)
                .build();

        UserEntity saved = userRepository.save(user);

        ServerEntity server = ServerEntity.builder()
                .name("Server 1")
                .owner(saved)
                .build();

        ServerEntity savedServer = serverRepository.save(server);

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
        assertTrue(channels.get(0).getName().equals("Channel 1"));
        assertTrue(channels.get(1).getName().equals("Channel 2"));
    }

    @Test
    void testFindByServerIdEmpty(){
        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .userName("owner-user")
                .password("password")
                .status(UserStatus.ONLINE)
                .build();

        UserEntity saved = userRepository.save(user);

        ServerEntity server = ServerEntity.builder()
                .name("Server 1")
                .owner(saved)
                .build();

        ServerEntity savedServer = serverRepository.save(server);

        List<ServerChannelEntity> channels = repository.findByServerId(savedServer.getId());

        assertEquals(channels.size(), 0);
    }
}
