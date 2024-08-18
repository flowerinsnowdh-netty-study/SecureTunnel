import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class CipherLengthTest {
    @Test
    void test() throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[32];
        sr.nextBytes(bytes);
        SecretKeySpec secretKey = new SecretKeySpec(bytes, "AES");
        bytes = new byte[32];
        sr.nextBytes(bytes);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(96, bytes));
        bytes = new byte[1022];
        sr.nextBytes(bytes);
        byte[] encrypted = cipher.doFinal(bytes);
        System.out.println(encrypted.length);
    }
}
