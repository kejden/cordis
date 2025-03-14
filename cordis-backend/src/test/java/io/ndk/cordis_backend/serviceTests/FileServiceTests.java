package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FileServiceTests {

    private String defaultFileName = "default.png";

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl();
        Field field = ReflectionUtils.findField(FileServiceImpl.class, "imageDir");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, fileService, "testUploads/");
    }

    @Test
    void testSaveFile_success() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        when(mockFile.getInputStream()).thenReturn(InputStream.nullInputStream());

        String fileName = fileService.saveFile(mockFile);

        assertNotNull(fileName);
        assertTrue(fileName.contains(".png"));
    }

    @Test
    void testSaveFile_throwsExceptionWhenIOExceptionOccurs() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        CustomException ex =
                assertThrows(CustomException.class, () -> fileService.saveFile(mockFile));
        assertEquals(BusinessErrorCodes.IMAGE_FETCH_FAILED, ex.getErrorCode());
    }

    @Test
    void testUpdateFile_success() throws IOException {
        String existingFileName = "oldFile_123.png";

        MultipartFile newFile = mock(MultipartFile.class);
        when(newFile.getOriginalFilename()).thenReturn("new.png");
        when(newFile.getInputStream()).thenReturn(InputStream.nullInputStream());

        String resultFile = fileService.updateFile(newFile, existingFileName);

        assertNotNull(resultFile);
        assertTrue(resultFile.contains("new.png"));
    }

    @Test
    void testUpdateFile_throwsExceptionWhenErrorOccurs() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("any.png");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        CustomException ex =
                assertThrows(CustomException.class, () -> fileService.updateFile(mockFile, "someOldFile.png"));
        assertEquals(BusinessErrorCodes.IMAGE_UPDATE_FAILED, ex.getErrorCode());
    }

    @Test
    void testDeleteFile_success() {
        assertDoesNotThrow(() -> fileService.deleteFile("whatever.png"));
    }

    @Test
    void testGetDefault_returnsDefaultName() {
        String defaultFile = fileService.getDefault();

        assertNotNull(defaultFile);
        assertEquals(defaultFileName, defaultFile);
    }

}
