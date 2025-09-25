package ttldd.labman.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ttldd.labman.service.EmailService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@labman.com}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException, UnsupportedEncodingException {
        String subject = "Mã OTP đặt lại mật khẩu - Lab Manager";
        
        // Sử dụng Thymeleaf để render template
        Context context = new Context();
        context.setVariable("otp", otp);
        String content = templateEngine.process("email/otp-template", context);
        
        log.info("Gửi mã OTP {} đến email: {}", otp, toEmail);
        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendPasswordChangedEmail(String toEmail, String userName, LocalDateTime changeTime) throws MessagingException, UnsupportedEncodingException {
        String subject = "Thông báo thay đổi mật khẩu - Lab Manager";
        
        String formattedTime = changeTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        // Sử dụng Thymeleaf để render template
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("changeTime", formattedTime);
        String content = templateEngine.process("email/password-changed-template", context);
        
        log.info("Gửi thông báo thay đổi mật khẩu đến email: {}", toEmail);
        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, "Lab Manager System");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info("Email đã được gửi thành công đến: {}", to);
            
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Lỗi khi gửi email đến {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }
}