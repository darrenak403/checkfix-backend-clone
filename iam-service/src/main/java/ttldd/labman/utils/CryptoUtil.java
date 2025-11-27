package ttldd.labman.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CryptoUtil {
    private static final String SECRET_KEY = "1a9733d7738894c5";

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public String decrypt(String encryptedBase64) {
        try {
            byte[] cipherData = Base64.getDecoder().decode(encryptedBase64);

            byte[] salted = "Salted__".getBytes(StandardCharsets.UTF_8);
            byte[] salt = Arrays.copyOfRange(cipherData, 8, 16);

            byte[][] keyAndIV = generateKeyAndIV(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    salt,
                    32,
                    16
            );
            byte[] key = keyAndIV[0];
            byte[] iv = keyAndIV[1];

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error decrypting: " + e.getMessage(), e);
        }
    }

    private byte[][] generateKeyAndIV(byte[] password, byte[] salt, int keyLen, int ivLen) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        byte[] key = new byte[keyLen];
        byte[] iv = new byte[ivLen];
        byte[] mdBuf = null;
        int nKey = 0;
        int nIv = 0;

        while (nKey < keyLen || nIv < ivLen) {
            md.update(mdBuf == null ? new byte[0] : mdBuf);
            md.update(password);
            md.update(salt, 0, 8);
            mdBuf = md.digest();

            int i = 0;
            while (nKey < keyLen && i < mdBuf.length) {
                key[nKey++] = mdBuf[i++];
            }
            while (nIv < ivLen && i < mdBuf.length) {
                iv[nIv++] = mdBuf[i++];
            }
        }

        return new byte[][]{key, iv};
    }
}
