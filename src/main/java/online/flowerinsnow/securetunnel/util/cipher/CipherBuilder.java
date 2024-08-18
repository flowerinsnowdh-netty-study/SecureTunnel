package online.flowerinsnow.securetunnel.util.cipher;

import online.flowerinsnow.securetunnel.exception.UncheckedException;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * <p>一个 {@link Cipher} 链式构建器</p>
 * <p>非线程安全</p>
 */
public class CipherBuilder {
    private final Cipher cipher;
    /**
     * <p>通过构造器 {@link Cipher#getInstance(String)} 构造</p>
     *
     * @param transformation the name of the transformation, e.g.,
     * <i>AES/CBC/PKCS5Padding</i>.
     * See the Cipher section in the <a href=
     *   "{@docRoot}/../specs/security/standard-names.html#cipher-algorithms">
     * Java Security Standard Algorithm Names Specification</a>
     * for information about standard transformation names.
     * @throws UncheckedException 当非运行时异常抛出时抛出
     */
    public CipherBuilder(@NotNull String transformation) throws UncheckedException {
        Objects.requireNonNull(transformation);
        try {
            this.cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new UncheckedException(e);
        }
    }

    /**
     * <p>通过方法 {@link Cipher#init(int, Key)} 初始化</p>
     *
     * @param opmode the operation mode of this {@code Cipher} object
     * (this is one of the following:
     * {@code ENCRYPT_MODE}, {@code DECRYPT_MODE},
     * {@code WRAP_MODE} or {@code UNWRAP_MODE})
     * @param key the key
     * @return 自己
     * @throws UncheckedException 当非运行时异常抛出时抛出
     */
    public @NotNull CipherBuilder init(int opmode, @NotNull Key key) throws UncheckedException {
        try {
            this.cipher.init(opmode, key);
            return this;
        } catch (InvalidKeyException e) {
            throw new UncheckedException(e);
        }
    }

    /**
     * <p>通过方法 {@link Cipher#init(int, Key, AlgorithmParameterSpec)} 初始化</p>
     *
     * @param opmode the operation mode of this {@link Cipher} object
     * (this is one of the following:
     * {@link Cipher#ENCRYPT_MODE}, {@link Cipher#DECRYPT_MODE},
     * {@link Cipher#WRAP_MODE} or {@link Cipher#UNWRAP_MODE})
     * @param key the encryption key
     * @param params the algorithm parameters
     * @return 自己
     * @throws UncheckedException 当非运行时异常抛出时抛出
     */
    public @NotNull CipherBuilder init(int opmode, @NotNull Key key, AlgorithmParameterSpec params) throws UncheckedException {
        try {
            this.cipher.init(opmode, key, params);
            return this;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new UncheckedException(e);
        }
    }

    /**
     * <p>获取最终结果</p>
     *
     * @return 最终结果
     */
    public @NotNull Cipher get() {
        return this.cipher;
    }
}
