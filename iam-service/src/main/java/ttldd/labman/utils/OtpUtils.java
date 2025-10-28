package ttldd.labman.utils;

import java.security.SecureRandom;

public class OtpUtils {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    

    public static String generateOtp() {
        int otp = 1000 + RANDOM.nextInt(9000); // Sinh số từ 1000 đến 9999
        return String.valueOf(otp);
    }
    

     // Kiểm tra tính hợp lệ của OTP

    public static boolean isValidOtp(String otp) {
        return otp != null && otp.matches("\\d{4}");
    }
}