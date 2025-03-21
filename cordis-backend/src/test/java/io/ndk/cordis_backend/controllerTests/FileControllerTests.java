package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.controller.FileController;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = FileController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FileControllerTests {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private io.ndk.cordis_backend.service.impl.customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private io.ndk.cordis_backend.service.FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello World".getBytes()
        );

        when(fileService.saveFile(any(MultipartFile.class))).thenReturn("saved-file.txt");

        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/file/upload")
                .file(mockFile))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("saved-file.txt"));

        verify(fileService, times(1)).saveFile(any(MultipartFile.class));
    }
}
