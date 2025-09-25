package ttldd.labman.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ttldd.labman.entity.PasswordResetOtp;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepo extends JpaRepository<PasswordResetOtp, Long> {
    
    /**
     * Tìm OTP mới nhất chưa được sử dụng theo email
     */
    Optional<PasswordResetOtp> findTopByEmailAndUsedFalseOrderByIdDesc(String email);
    
    /**
     * Tìm OTP theo email và mã OTP
     */
    Optional<PasswordResetOtp> findByEmailAndOtpAndUsedFalse(String email, String otp);
    
    /**
     * Xóa tất cả OTP cũ của email
     */
    @Modifying
    @Query("DELETE FROM PasswordResetOtp p WHERE p.email = :email")
    void deleteAllByEmail(@Param("email") String email);
}