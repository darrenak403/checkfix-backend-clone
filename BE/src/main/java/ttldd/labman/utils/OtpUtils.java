package ttldd.labman.utils;

import java.security.SecureRandom;

public class OtpUtils {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * Tạo mã OTP 4 chữ số ngẫu nhiên
     * @return chuỗi OTP 4 chữ số
     */
    public static String generateOtp() {
        int otp = 1000 + RANDOM.nextInt(9000); // Sinh số từ 1000 đến 9999
        return String.valueOf(otp);
    }
    
    /**
     * Kiểm tra tính hợp lệ của OTP
     * @param otp mã OTP cần kiểm tra
     * @return true nếu OTP hợp lệ (4 chữ số), false nếu không hợp lệ
     */
    public static boolean isValidOtp(String otp) {
        return otp != null && otp.matches("\\d{4}");
    }
}