package online.flowerinsnow.securetunnel.util.cipher;

import online.flowerinsnow.securetunnel.exception.UnexpectedException;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import java.lang.reflect.Field;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public abstract class CipherUtils {
    private CipherUtils() {
    }

    public static class EC {
        /**
         * <p>封锁构造方法，不允许实例化</p>
         */
        private EC() {
        }

        /**
         * <p>算法名称</p>
         */
        @NotNull public static final String ALGORITHM = "EC";

        /**
         * <p>通过 {@link KeyPairGenerator} 随机生成椭圆曲线密钥对</p>
         *
         * @return 随机生成椭圆曲线密钥对
         */
        public static @NotNull KeyPair genKeyPair() {
            KeyPairGenerator kpg;
            try {
                kpg = KeyPairGenerator.getInstance(EC.ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new UnexpectedException(e);
            }
            kpg.initialize(256);
            return kpg.genKeyPair();
        }

        /**
         * <p>通过 {@link KeyFactory} 反序列化 X509 公钥</p>
         * @param encoded 密钥编码
         * @return 公钥
         * @throws InvalidKeySpecException if the given key specification
         * is inappropriate for this key factory to produce a public key.
         */
        public static @NotNull PublicKey deserializePublic(byte @NotNull [] encoded) throws InvalidKeySpecException {
            Objects.requireNonNull(encoded);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory kf;
            try {
                kf = KeyFactory.getInstance(EC.ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new UnexpectedException(e);
            }
            return kf.generatePublic(keySpec);
        }

        /**
         * <p>通过 {@link KeyFactory} 反序列化 PKCS8 私钥</p>
         *
         * @param encoded 密钥编码
         * @return 私钥
         * @throws InvalidKeySpecException if the given key specification
         * is inappropriate for this key factory to produce a private key.
         */
        public static @NotNull PrivateKey deserializePrivate(byte @NotNull [] encoded) throws InvalidKeySpecException {
            Objects.requireNonNull(encoded);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf;
            try {
                kf = KeyFactory.getInstance(EC.ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new UnexpectedException(e);
            }
            return kf.generatePrivate(keySpec);
        }
    }

    /**
     * <p><a href="https://openstd.samr.gov.cn/bzgk/gb/newGbInfo?hcno=45B1A67F20F3BF339211C391E9278F5E">信息安全技术 SM3密码杂凑算法 (GB/T 32905——2016)</a> 相关工具类</p>
     */
    public static class SM3 {
        /**
         * <p>封锁构造方法，不允许实例化</p>
         */
        private SM3() {
        }

        /**
         * <p>计算 {@code data} 的 SM3 哈希</p>
         * @param data 数据
         * @return {@code data} 的 SM3 哈希
         */
        public static byte[] sm3(byte @NotNull [] data) {
            Objects.requireNonNull(data);
            MessageDigest sm3;
            try {
                sm3 = MessageDigest.getInstance("SM3");
            } catch (NoSuchAlgorithmException e) {
                throw new UnexpectedException(e);
            }
            return sm3.digest(data);
        }
    }

    public static class AES {
        /**
         * <p>封锁构造方法，不允许实例化</p>
         */
        private AES() {
        }

        /**
         * <p>AES+ECB+PKCS7Padding 算法套件名称</p>
         */
        @NotNull public static final String AES_ECB_PKCS7PADDING = "AES/ECB/PKCS7Padding";
        /**
         * <p>AES+GCM 算法套件名称</p>
         */
        @NotNull public static final String AES_GCM = "AES/GCM/NoPadding";
    }

    public static class GCM {
        /**
         * <p>封锁构造方法，不允许实例化</p>
         */
        private GCM() {
        }

        /**
         * <p>默认 GCM 验证块大小</p>
         */
        public static final int TLEN = 96;
    }

    /**
     * <p>反射获取 {@code cipher} 的私有 {@link Cipher#opmode} 字段</p>
     *
     * @param cipher 密码引擎
     * @return {@code cipher} 的私有 {@link Cipher#opmode} 字段
     */
    @SuppressWarnings("JavadocReference")
    public static int getOpmode(@NotNull Cipher cipher) {
        Objects.requireNonNull(cipher);
        Field field;
        try {
            field = Cipher.class.getDeclaredField("opmode");
        } catch (NoSuchFieldException e) {
            throw new UnexpectedException(e);
        }
        field.setAccessible(true);
        try {
            return field.getInt(cipher);
        } catch (IllegalAccessException e) {
            throw new UnexpectedException(e);
        }
    }
}
