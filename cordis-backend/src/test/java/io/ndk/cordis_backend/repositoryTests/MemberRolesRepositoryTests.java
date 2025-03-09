package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.MemberRolesEntity;
import io.ndk.cordis_backend.entity.RoleEntity;
import io.ndk.cordis_backend.entity.ServerEntity;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.repository.MemberRolesRepository;
import io.ndk.cordis_backend.repository.RoleRepository;
import io.ndk.cordis_backend.repository.ServerRepository;
import io.ndk.cordis_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class MemberRolesRepositoryTests {

    @Autowired
    private MemberRolesRepository memberRolesRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServerRepository serverRepository;

    private UserEntity user;
    private RoleEntity role;
    private ServerEntity server;

    @BeforeEach
    void setUp() {
        memberRolesRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        serverRepository.deleteAll();

        user = userRepository.save(
                UserEntity.builder()
                    .email("test@example.com")
                    .userName("tester")
                    .password("password")
                    .status(UserStatus.ONLINE)
                    .build());

        server = serverRepository.save(
                ServerEntity.builder()
                        .owner(user)
                        .name("TestServer")
                        .build());

        role = roleRepository.save(
                RoleEntity.builder()
                        .name("USER")
                        .build());
    }

    @Test
    void testFindByUserIdAndServerId() {
        MemberRolesEntity saved = memberRolesRepository.save(
                MemberRolesEntity.builder()
                        .user(user)
                        .server(server)
                        .role(role)
                        .build()
        );

        assertTrue(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId()).isPresent());
    }

    @Test
    void testFindByUserIdAndServerIdInvalid(){
        assertFalse(memberRolesRepository.findByUserIdAndServerId(user.getId(), server.getId()).isPresent());
    }

    @Test
    void testDeleteByUserIdAndServerId() {
        memberRolesRepository.save(
                MemberRolesEntity.builder()
                        .user(user)
                        .server(server)
                        .role(role)
                        .build()
        );
        memberRolesRepository.deleteByUserIdAndServerId(user.getId(), server.getId());

        assertFalse(memberRolesRepository.existsByUserIdAndServerId(user.getId(), server.getId()));
    }

    @Test
    void testExistsByUserIdAndServerId() {
        memberRolesRepository.save(
                MemberRolesEntity.builder()
                        .user(user)
                        .server(server)
                        .role(role)
                        .build()
        );

        assertTrue(memberRolesRepository.existsByUserIdAndServerId(user.getId(), server.getId()));
    }

    @Test
    void testFindByUserId() {
        memberRolesRepository.save(
                MemberRolesEntity.builder()
                        .user(user)
                        .server(server)
                        .role(role)
                        .build()
        );

        assertEquals(1, memberRolesRepository.findByUserId(user.getId()).size());
    }

    @Test
    void testFindByServerId() {
        memberRolesRepository.save(
                MemberRolesEntity.builder()
                        .user(user)
                        .server(server)
                        .role(role)
                        .build()
        );

        assertEquals(1, memberRolesRepository.findByServerId(server.getId()).size());
    }

}
