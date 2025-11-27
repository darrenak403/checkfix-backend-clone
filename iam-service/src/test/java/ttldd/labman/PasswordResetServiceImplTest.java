package ttldd.labman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.labman.service.imp.PasswordResetServiceImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ============== PasswordResetServiceImpl Tests ==============
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private ttldd.labman.repo.UserRepo userRepo;

    @Mock
    private ttldd.labman.repo.PasswordResetOtpRepo passwordResetOtpRepo;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private ttldd.labman.producer.NotificationProducer notificationProducer;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private ttldd.labman.dto.request.ForgotPasswordRequest forgotPasswordRequest;
    private ttldd.labman.dto.request.ResetPasswordRequest resetPasswordRequest;
    private ttldd.labman.entity.User user;

    @BeforeEach
    void setUp() {
        forgotPasswordRequest = new ttldd.labman.dto.request.ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("test@example.com");

        resetPasswordRequest = new ttldd.labman.dto.request.ResetPasswordRequest();
        resetPasswordRequest.setEmail("test@example.com");
        resetPasswordRequest.setOtp("1234");
        resetPasswordRequest.setNewPassword("newPassword123");

        user = new ttldd.labman.entity.User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
    }

    @Test
    void sendOtp_ValidEmail_ReturnsSuccess() {
        // Arrange
        when(userRepo.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(passwordResetOtpRepo.save(any())).thenReturn(new ttldd.labman.entity.PasswordResetOtp());

        // Act
        var response = passwordResetService.sendOtp(forgotPasswordRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Mã OTP đã được gửi đến email của bạn", response.getMessage());
        verify(passwordResetOtpRepo, times(1)).save(any());
    }

    @Test
    void sendOtp_EmailNotFound_ReturnsFailure() {
        // Arrange
        when(userRepo.findByEmail(anyString())).thenReturn(java.util.Optional.empty());

        // Act
        var response = passwordResetService.sendOtp(forgotPasswordRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Email không tồn tại trong hệ thống", response.getMessage());
        verify(passwordResetOtpRepo, never()).save(any());
    }

    @Test
    void resetPassword_ValidOtp_ReturnsSuccess() {
        // OTP còn hạn
        ttldd.labman.entity.PasswordResetOtp otp = ttldd.labman.entity.PasswordResetOtp.builder()
                .email("test@example.com")
                .otp("1234")
                .used(false)
                .expiryTime(LocalDateTime.now().plusMinutes(10)) // Còn 10 phút
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(passwordResetOtpRepo.findByEmailAndOtpAndUsedFalse(anyString(), anyString()))
                .thenReturn(java.util.Optional.of(otp));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        var response = passwordResetService.resetPassword(resetPasswordRequest);

        assertTrue(response.isSuccess());
        assertEquals("Mật khẩu đã được đặt lại thành công", response.getMessage());
        verify(userRepo, times(1)).save(any());
        verify(passwordResetOtpRepo, times(1)).save(any());
    }

    @Test
    void resetPassword_ExpiredOtp_ReturnsFailure() {
        // OTP đã hết hạn
        ttldd.labman.entity.PasswordResetOtp otp = ttldd.labman.entity.PasswordResetOtp.builder()
                .email("test@example.com")
                .otp("1234")
                .used(false)
                .expiryTime(LocalDateTime.now().minusMinutes(10)) // Hết hạn từ 10 phút trước
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(passwordResetOtpRepo.findByEmailAndOtpAndUsedFalse(anyString(), anyString()))
                .thenReturn(java.util.Optional.of(otp));

        var response = passwordResetService.resetPassword(resetPasswordRequest);

        assertFalse(response.isSuccess());
        // Assert error message về OTP hết hạn
    }

    @Test
    void resetPassword_UsedOtp_ReturnsFailure() {
        // OTP đã được sử dụng
        ttldd.labman.entity.PasswordResetOtp otp = ttldd.labman.entity.PasswordResetOtp.builder()
                .email("test@example.com")
                .otp("1234")
                .used(true) // Đã sử dụng
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(passwordResetOtpRepo.findByEmailAndOtpAndUsedFalse(anyString(), anyString()))
                .thenReturn(java.util.Optional.empty()); // Không tìm thấy vì used=true

        var response = passwordResetService.resetPassword(resetPasswordRequest);

        assertFalse(response.isSuccess());
        // Assert error message về OTP không hợp lệ
    }

    @Test
    void resetPassword_InvalidOtp_ReturnsFailure() {
        // Arrange
        when(userRepo.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(passwordResetOtpRepo.findByEmailAndOtpAndUsedFalse(anyString(), anyString()))
                .thenReturn(java.util.Optional.empty());

        // Act
        var response = passwordResetService.resetPassword(resetPasswordRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Mã OTP không hợp lệ hoặc đã hết hạn", response.getMessage());
        verify(userRepo, never()).save(any());
    }
}
