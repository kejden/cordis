package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.Mappers.impl.UserDtoMapper;
import io.ndk.cordis_backend.Mappers.impl.UserMapperImpl;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.EditUserRequest;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.dto.response.SignInResponse;
import io.ndk.cordis_backend.entity.UserEntity;
import io.ndk.cordis_backend.enums.UserStatus;
import io.ndk.cordis_backend.handler.BusinessErrorCodes;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private UserMapperImpl accountSignUpMapper;

    @Mock
    private UserDtoMapper userDtoMapper; //Mapper<UserEntity, UserDto>

    @Mock
    private FileService fileService;

    @InjectMocks
    private UserServiceImpl userService;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Test
    void testSignUp() {
        AccountSignUp dto = AccountSignUp.builder()
                .email("test@example.com")
                .password("password123")
                .userName("testUser")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .userName("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        when(accountSignUpMapper.mapTo(any(UserEntity.class))).thenReturn(dto);

        AccountSignUp result = userService.signUp(dto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testUser", result.getUserName());
    }

    @Test
    void signUp_WithExistingEmail_ShouldThrowException() {
        AccountSignUp dto = new AccountSignUp();
        dto.setEmail("existing@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(CustomException.class, () -> userService.signUp(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSignIn() {
        SignInRequest signInRequest = SignInRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .userName("testUser")
                .status(UserStatus.OFFLINE)
                .build();

        when(userRepository.findByEmail(signInRequest.getEmail())).thenReturn(Optional.of(userEntity));

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        SignInResponse response = userService.signIn(signInRequest);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("testUser", response.getUserName());
        assertEquals(UserStatus.ONLINE, userEntity.getStatus());
    }

    @Test
    void testSignInUserNotFound() {
        SignInRequest signInRequest = SignInRequest.builder()
                .email("missing@example.com")
                .password("password")
                .build();
        when(userRepository.findByEmail(signInRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.signIn(signInRequest));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testSignInUserBadCredentials() {
        SignInRequest signInRequest = SignInRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .userName("testUser")
                .status(UserStatus.OFFLINE)
                .build();

        when(userRepository.findByEmail(signInRequest.getEmail())).thenReturn(Optional.of(userEntity));

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        CustomException ex = assertThrows(CustomException.class, () -> userService.signIn(signInRequest));
        assertEquals(BusinessErrorCodes.BAD_CREDENTIALS, ex.getErrorCode());
    }

    @Test
    void testLogout() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@example.com")
                .status(UserStatus.ONLINE)
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        userService.logout("test@example.com");

        assertEquals(UserStatus.OFFLINE, userEntity.getStatus());
        verify(userRepository).save(userEntity);
    }

    @Test
    void testLogoutUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.logout("missing@example.com"));
    }

    @Test
    void testUpdateUser() {
        String email = "test@example.com";
        UserDto dto = new UserDto();
        dto.setUserName("updatedName");
        dto.setProfileImage("updatedProfile.png");

        UserEntity existingUser = UserEntity.builder()
                .email(email)
                .userName("oldName")
                .profileImage("oldProfile.png")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto updatedDto = new UserDto();
        updatedDto.setUserName("updatedName");
        updatedDto.setProfileImage("updatedProfile.png");
        when(userDtoMapper.mapTo(any(UserEntity.class))).thenReturn(updatedDto);

        UserDto result = userService.updateUser(email, dto);

        assertEquals("updatedName", result.getUserName());
        assertEquals("updatedProfile.png", result.getProfileImage());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.updateUser("missing@example.com", new UserDto()));
    }

    @Test
    void testEditUser() {
        String email = "test@example.com";
        EditUserRequest request = new EditUserRequest();
        request.setUsername("newUsername");

        UserEntity existingUser = UserEntity.builder()
                .email(email)
                .userName("oldUsername")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto mappedUser = new UserDto();
        mappedUser.setUserName("newUsername");
        when(userDtoMapper.mapTo(any(UserEntity.class))).thenReturn(mappedUser);

        UserDto result = userService.editUser(request, email);

        assertEquals("newUsername", result.getUserName());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testEditUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.editUser(new EditUserRequest(), "missing@example.com"));
    }

    @Test
    void testUpdateUserImageProfile() {
        MultipartFile file = mock(MultipartFile.class);
        Principal principal = () -> "test@example.com";
        UserEntity user = UserEntity.builder()
                .email("test@example.com")
                .profileImage("oldImage.png")
                .build();

        when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));
        when(fileService.updateFile(file, user.getProfileImage())).thenReturn("newImage.png");

        String result = userService.updateUserImageProfile(file, principal);

        assertTrue(result.contains("newImage.png"));
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserImageProfileNotFound() {
        MultipartFile file = mock(MultipartFile.class);
        Principal principal = () -> "missing@example.com";

        when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.updateUserImageProfile(file, principal));
        verify(fileService, never()).updateFile(any(), anyString());
    }

}
