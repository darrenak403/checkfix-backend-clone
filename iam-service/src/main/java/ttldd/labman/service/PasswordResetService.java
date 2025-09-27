package ttldd.labman.service;

import ttldd.labman.dto.request.ForgotPasswordRequest;
import ttldd.labman.dto.request.ResetPasswordRequest;
import ttldd.labman.dto.response.ForgotPasswordResponse;
import ttldd.labman.dto.response.ResetPasswordResponse;

public interface PasswordResetService {

    /**
     * Gửi mã OTP đến email người dùng để đặt lại mật khẩu
     */
    ForgotPasswordResponse sendOtp(ForgotPasswordRequest request);

    /**
     * Xác thực mã OTP và đặt lại mật khẩu mới
     */
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}