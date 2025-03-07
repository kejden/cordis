package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.repository.UserRepository;
import io.ndk.cordis_backend.service.FileService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Mapper<UserEntity, AccountSignUp> accountSignUpMapper;

    @Mock
    private Mapper<UserEntity, UserDto> userDtoMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private UserServiceImpl userService;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Test
    void signUp_WithNewEmail_ShouldSaveUser() {
        AccountSignUp dto = AccountSignUp.builder()
                .email("email@email.com")
                .password("password123")
                .userName("test_user")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(fileService.getDefault()).thenReturn("default-image.jpg");

        UserEntity savedUser = UserEntity.builder()
                .email(dto.getEmail())
                .userName(dto.getUserName())
                .password("encodedPassword")
                .status(UserStatus.OFFLINE)
                .profileImage("default-image.jpg")
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(accountSignUpMapper.mapTo(savedUser)).thenReturn(dto);

        AccountSignUp result = userService.signUp(dto);

        assertNotNull(result);
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode("password123");
        verify(fileService).getDefault();
    }

    @Test
    void signUp_WithExistingEmail_ShouldThrowException() {
        AccountSignUp dto = new AccountSignUp();
        dto.setEmail("existing@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(CustomException.class, () -> userService.signUp(dto));
        verify(userRepository, never()).save(any());
    }

}
