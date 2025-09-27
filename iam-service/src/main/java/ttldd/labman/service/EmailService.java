package ttldd.labman.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public interface EmailService {

    /**
     * Gửi email chứa mã OTP để reset mật khẩu
     */
    void sendOtpEmail(String toEmail, String otp) throws MessagingException, UnsupportedEncodingException;

    /**
     * Gửi email thông báo đổi mật khẩu thành công
     */
    void sendPasswordChangedEmail(String toEmail, String userName, LocalDateTime changeTime) throws MessagingException, UnsupportedEncodingException;

    /**
     * Gửi email tùy chỉnh
     */
    void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException;
}