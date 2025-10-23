package ttldd.labman.service.imp;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ttldd.labman.dto.request.ForgotPasswordRequest;
import ttldd.labman.dto.request.ResetPasswordRequest;
import ttldd.labman.dto.response.ForgotPasswordResponse;
import ttldd.labman.dto.response.ResetPasswordResponse;
import ttldd.labman.entity.PasswordResetOtp;
import ttldd.labman.entity.User;
import ttldd.labman.producer.NotificationProducer;
import ttldd.labman.repo.PasswordResetOtpRepo;
import ttldd.labman.repo.UserRepo;
import ttldd.labman.service.EmailService;
import ttldd.labman.service.PasswordResetService;
import ttldd.labman.utils.OtpUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepo userRepo;
    private final PasswordResetOtpRepo passwordResetOtpRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationProducer notificationProducer;

    @Override
    @Transactional
    public ForgotPasswordResponse sendOtp(ForgotPasswordRequest request) {
        try {
            log.info("Bắt đầu xử lý yêu cầu quên mật khẩu cho email: {}", request.getEmail());
            
            // Kiểm tra xem email có tồn tại trong hệ thống không
            Optional<User> userOptional = userRepo.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                log.warn("Email không tồn tại trong hệ thống: {}", request.getEmail());
                return ForgotPasswordResponse.builder()
                        .success(false)
                        .message("Email không tồn tại trong hệ thống")
                        .email(request.getEmail())
                        .build();
            }
            
            // Xóa tất cả OTP cũ của email này (nếu có)
//            passwordResetOtpRepo.deleteAllByEmail(request.getEmail());

            // Tạo mã OTP 4 chữ số
            String otp = OtpUtils.generateOtp();
            
            // Lưu OTP vào cơ sở dữ liệu
            PasswordResetOtp passwordResetOtp = PasswordResetOtp.builder()
                    .email(request.getEmail())
                    .otp(otp)
                    .used(false)
                    .build();
            
            passwordResetOtpRepo.save(passwordResetOtp);
            
            // Gửi email chứa OTP
            try {
                notificationProducer.sendEmail(
                        "send-email",
                        userOptional.get().getEmail(),
                        "[L.M.S] - Mã khôi phục mật khẩu",
                        "FORGOT_PASSWORD_EMAIL",
                        Map.of("otp", passwordResetOtp.getOtp())
                );

                return ForgotPasswordResponse.builder()
                        .success(true)
                        .message("Mã OTP đã được gửi đến email của bạn")
                        .email(request.getEmail())
                        .build();

            } catch (KafkaProducerException e) {
                log.error("Lỗi khi gửi email OTP: {}", e.getMessage(), e);
                return ForgotPasswordResponse.builder()
                        .success(false)
                        .message("Không thể gửi email. Vui lòng thử lại sau.")
                        .email(request.getEmail())
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Lỗi không xác định khi xử lý quên mật khẩu: {}", e.getMessage(), e);
            return ForgotPasswordResponse.builder()
                    .success(false)
                    .message("Đã xảy ra lỗi. Vui lòng thử lại sau.")
                    .email(request.getEmail())
                    .build();
        }
    }

    @Override
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        try {
            log.info("Bắt đầu xử lý reset mật khẩu cho email: {}", request.getEmail());
            
            // Tìm user theo email
            Optional<User> userOptional = userRepo.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                log.warn("Email không tồn tại trong hệ thống: {}", request.getEmail());
                return ResetPasswordResponse.builder()
                        .success(false)
                        .message("Email không tồn tại trong hệ thống")
                        .email(request.getEmail())
                        .build();
            }
            
            User user = userOptional.get();
            
            // Kiểm tra OTP
            Optional<PasswordResetOtp> otpOptional = passwordResetOtpRepo.findByEmailAndOtpAndUsedFalse(
                    request.getEmail(), request.getOtp());
            
            if (otpOptional.isEmpty()) {
                log.warn("Mã OTP không hợp lệ cho email: {}", request.getEmail());
                return ResetPasswordResponse.builder()
                        .success(false)
                        .message("Mã OTP không hợp lệ hoặc đã hết hạn")
                        .email(request.getEmail())
                        .build();
            }
            
            PasswordResetOtp passwordResetOtp = otpOptional.get();
            
            // Kiểm tra thời gian hết hạn
            if (passwordResetOtp.isExpired()) {
                log.warn("Mã OTP đã hết hạn cho email: {}", request.getEmail());
                return ResetPasswordResponse.builder()
                        .success(false)
                        .message("Mã OTP đã hết hạn")
                        .email(request.getEmail())
                        .build();
            }
            
            // Cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepo.save(user);
            
            // Đánh dấu OTP đã được sử dụng
            passwordResetOtp.setUsed(true);
            passwordResetOtpRepo.save(passwordResetOtp);
            
            // Gửi email thông báo mật khẩu đã được thay đổi
            try {
                String formattedTime = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                Map<String, Object> emailParams = Map.of(
                        "userName", user.getFullName(),
                        "timeChanged", formattedTime
                );
                notificationProducer.sendEmail(
                        "send-email",
                        user.getEmail(),
                        "[L.M.S] - Thông báo đổi mật khẩu",
                        "RESET_PASSWORD_EMAIL",
                        emailParams
                );
            } catch (Exception e) {
                log.warn("Không thể gửi email thông báo đổi mật khẩu: {}", e.getMessage());
            }
            
            log.info("Đặt lại mật khẩu thành công cho email: {}", request.getEmail());
            return ResetPasswordResponse.builder()
                    .success(true)
                    .message("Mật khẩu đã được đặt lại thành công")
                    .email(request.getEmail())
                    .build();
            
        } catch (Exception e) {
            log.error("Lỗi khi đặt lại mật khẩu: {}", e.getMessage(), e);
            return ResetPasswordResponse.builder()
                    .success(false)
                    .message("Đã xảy ra lỗi khi đặt lại mật khẩu")
                    .email(request.getEmail())
                    .build();
        }
    }
}