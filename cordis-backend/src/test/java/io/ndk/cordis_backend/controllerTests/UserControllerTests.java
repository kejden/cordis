package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.UserController;
import io.ndk.cordis_backend.dto.UserDto;
import io.ndk.cordis_backend.dto.request.EditUserRequest;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.UserService;
import io.ndk.cordis_backend.service.impl.customUserDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = true)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CookieService cookieService;

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testEditProfile_Success() throws Exception {
        EditUserRequest request = new EditUserRequest();
        request.setUsername("NewUsername");

        UserDto resultDto = UserDto.builder()
                .userName("NewUser").email("test@example.com").build();

        when(userService.editUser(any(EditUserRequest.class), eq("testUser@example.com")))
               .thenReturn(resultDto);

        mockMvc.perform(post("/profile/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"NewUsername\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("NewUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1))
                .editUser(any(EditUserRequest.class), eq("testUser@example.com"));
    }

    @Test
    @WithMockUser(username = "testUser@example.com")
    void testUploadImageToFileSystem_Success() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "some_fake_image_bytes".getBytes()
        );

        when(userService.updateUserImageProfile(
                any(MultipartFile.class),
                any(Principal.class))
        ).thenReturn("http://cdn.example.com/test.png");

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/image")
                .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(content().string("http://cdn.example.com/test.png"));

        verify(userService, times(1))
                .updateUserImageProfile(any(MultipartFile.class), any(Principal.class));
    }

}
