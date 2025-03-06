package io.ndk.cordis_backend.repositoryTests;

import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save a UserEntity and retrieve it by email")
    void testSaveAndFindByEmail() {
        String email = "test@example.com";
        String password = "secret123";
        String username = "test_user";

        UserEntity user = UserEntity.builder()
                .id(0L)
                .email(email)
                .password(password)
                .userName(username)
                .status(UserStatus.OFFLINE)
                .profileImage("profile_image.png")
                .build();

        UserEntity saved = userRepository.save(user);
        assertNotNull(saved.getId(), "Saved user should have an ID");

        UserEntity found = userRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(found, "User should be found by email");
        assertEquals("test_user", found.getUserName());
    }
}
