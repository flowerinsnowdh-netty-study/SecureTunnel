package online.flowerinsnow.securetunnel.util.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import online.flowerinsnow.securetunnel.exception.DecryptFailureException;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.util.Objects;

/**
 * <p>有关 Netty 缓冲对象的工具类</p>
 */
public class BufUtils {
    /**
     * <p>封锁构造方法，不允许实例化</p>
     */
    private BufUtils() {
    }

    /**
     * <p>分配一个 {@link ByteBuf}，并将数据 {@code data} 写入其中</p>
     *
     * @param allocator ByteBuf 分配器，建议使用通道池化分配器
     * @param data 数据
     * @return 写好的 {@link ByteBuf}
     */
    public static @NotNull ByteBuf fromData(@NotNull ByteBufAllocator allocator, byte @NotNull [] data) {
        Objects.requireNonNull(allocator);
        Objects.requireNonNull(data);
        ByteBuf buffer = allocator.buffer();
        buffer.writeBytes(data);
        return buffer;
    }

    /**
     * <p>分配一个 {@link ByteBuf}，并将数据 {@code data} 解密后写入其中</p>
     *
     * @param allocator ByteBuf 分配器，
     * @param data 数据
     * @param cipher 密码引擎
     * @return 写好的 {@link ByteBuf}
     */
    public static @NotNull ByteBuf fromEncrypt(@NotNull ByteBufAllocator allocator, byte @NotNull [] data, @NotNull Cipher cipher) throws DecryptFailureException {
        Objects.requireNonNull(allocator);
        Objects.requireNonNull(data);
        Objects.requireNonNull(cipher);
        if (CipherUtils.getOpmode(cipher) != Cipher.ENCRYPT_MODE) {
            throw new IllegalArgumentException("cipher isn't encrypt mode");
        }
        try {
            byte[] result = cipher.doFinal(data);
            return BufUtils.fromCipher(allocator, result, cipher);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptFailureException(e);
        }
    }

    /**
     * <p>分配一个 {@link ByteBuf}，并将数据 {@code data} 加密后写入其中</p>
     *
     * @param allocator ByteBuf 分配器，
     * @param data 数据
     * @param cipher 密码引擎
     * @return 写好的 {@link ByteBuf}
     * @throws DecryptFailureException 当解密失败时抛出
     */
    public static @NotNull ByteBuf fromDecrypt(@NotNull ByteBufAllocator allocator, byte @NotNull [] data, @NotNull Cipher cipher) throws DecryptFailureException {
        Objects.requireNonNull(allocator);
        Objects.requireNonNull(data);
        Objects.requireNonNull(cipher);
        if (CipherUtils.getOpmode(cipher) != Cipher.DECRYPT_MODE) {
            throw new IllegalArgumentException("cipher isn't encrypt mode");
        }
        try {
            return BufUtils.fromCipher(allocator, data, cipher);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptFailureException(e);
        }
    }

    /**
     * <p>分配一个 {@link ByteBuf}，并将数据 {@code data} 通过 {@code cipher} 加密/解密后写入其中</p>
     *
     * @param allocator ByteBuf 分配器，
     * @param data 数据
     * @param cipher 密码引擎
     * @return 写好的 {@link ByteBuf}
     * @throws IllegalBlockSizeException if this cipher is a block cipher,
     * no padding has been requested (only in encryption mode), and the total
     * input length of the data processed by this cipher is not a multiple of
     * block size; or if this encryption algorithm is unable to
     * process the input data provided.
     * @throws BadPaddingException if this {@code Cipher} object is in
     * decryption mode, and (un)padding has been requested, but the
     * decrypted data is not bounded by the appropriate padding bytes
     */
    private static @NotNull ByteBuf fromCipher(@NotNull ByteBufAllocator allocator, byte @NotNull [] data, @NotNull Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        Objects.requireNonNull(allocator);
        Objects.requireNonNull(data);
        Objects.requireNonNull(cipher);
        byte[] result = cipher.doFinal(data);
        return BufUtils.fromData(allocator, result);
    }

    /**
     * <p>将 {@code buf} 中的可读内容全部读出</p>
     * <p>不会释放 {@code buf}</p>
     * @param buf 数据缓冲
     * @return 读出的数据
     */
    public static byte @NotNull [] readAll(@NotNull ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return data;
    }
}
