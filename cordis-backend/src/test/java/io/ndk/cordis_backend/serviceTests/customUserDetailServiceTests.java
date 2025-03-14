package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.impl.customUserDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class customUserDetailServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private customUserDetailService userDetailService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        UserEntity userEntity = UserEntity.builder()
                .email("exists@example.com")
                .password("encodedPass")
                .build();
        when(userRepository.findByEmail("exists@example.com"))
                .thenReturn(Optional.of(userEntity));

        UserDetails details = userDetailService.loadUserByUsername("exists@example.com");

        assertNotNull(details);
        assertEquals("exists@example.com", details.getUsername());
        assertEquals("encodedPass", details.getPassword());
        verify(userRepository, times(1)).findByEmail("exists@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername("missing@example.com"));
        verify(userRepository, times(1)).findByEmail("missing@example.com");
    }
}
