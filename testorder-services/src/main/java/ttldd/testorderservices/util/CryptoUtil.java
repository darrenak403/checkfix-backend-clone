package ttldd.testorderservices.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CryptoUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String OPENSSL_HEADER = "Salted__";

    @Value("${app.security.encryption-key}")
    private String SECRET_KEY;

    public String encrypt(String value) {
        try {
            if (value == null) return null;

            byte[] salt = new byte[8];
            new SecureRandom().nextBytes(salt);

            byte[][] keyAndIV = deriveKeyAndIV(SECRET_KEY.getBytes(StandardCharsets.UTF_8), salt);
            byte[] key = keyAndIV[0];
            byte[] iv = keyAndIV[1];

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] headerBytes = OPENSSL_HEADER.getBytes(StandardCharsets.UTF_8);
            byte[] finalBytes = new byte[headerBytes.length + salt.length + encryptedBytes.length];

            System.arraycopy(headerBytes, 0, finalBytes, 0, headerBytes.length);
            System.arraycopy(salt, 0, finalBytes, headerBytes.length, salt.length);
            System.arraycopy(encryptedBytes, 0, finalBytes, headerBytes.length + salt.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(finalBytes);

        } catch (Exception e) {
            throw new IllegalArgumentException("Error encrypting value", e);
        }
    }

    public  String encryptForURL(String value) {
        try {
            String encrypted = encrypt(value);
            return URLEncoder.encode(encrypted, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error encoding URL", e);
        }
    }
    private byte[][] deriveKeyAndIV(byte[] password, byte[] salt) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] dx = new byte[0];
        byte[] combined = new byte[48];
        int bytesGenerated = 0;

        while (bytesGenerated < 48) {
            md5.update(dx);
            md5.update(password);
            md5.update(salt);
            dx = md5.digest();

            int count = Math.min(dx.length, 48 - bytesGenerated);
            System.arraycopy(dx, 0, combined, bytesGenerated, count);
            bytesGenerated += count;
        }

        byte[] key = Arrays.copyOfRange(combined, 0, 32);
        byte[] iv = Arrays.copyOfRange(combined, 32, 48);
        return new byte[][]{key, iv};
    }
}
