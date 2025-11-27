package ttldd.labman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.labman.service.imp.CloudinaryServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

// ============== CloudinaryServiceImpl Tests ==============
@ExtendWith(MockitoExtension.class)
class CloudinaryServiceImplTest {

    @Mock
    private com.cloudinary.Cloudinary cloudinary;

    @Mock
    private com.cloudinary.Uploader uploader;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    @Mock
    private org.springframework.web.multipart.MultipartFile file;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadFile_Success_ReturnsUrl() throws Exception {
        // Arrange
        String expectedUrl = "https://cloudinary.com/image.jpg";
        byte[] fileBytes = "test".getBytes();
        java.util.Map<String, Object> uploadResult = new java.util.HashMap<>();
        uploadResult.put("secure_url", expectedUrl);

        when(file.getBytes()).thenReturn(fileBytes);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        // Act
        String result = cloudinaryService.uploadFile(file);

        // Assert
        assertEquals(expectedUrl, result);
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadFile_IOException_ThrowsException() throws Exception {
        // Arrange
        when(file.getBytes()).thenThrow(new java.io.IOException("File read error"));

        // Act & Assert
        assertThrows(java.io.IOException.class, () -> cloudinaryService.uploadFile(file));
    }
}
