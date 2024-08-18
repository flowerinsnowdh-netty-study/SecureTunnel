package online.flowerinsnow.securetunnel.object;

import javax.crypto.spec.GCMParameterSpec;

/**
 * <p>固定 {@code tLen=96} 的 {@link GCMParameterSpec}</p>
 */
public class DefaultGCMParamSpec extends GCMParameterSpec {
    /**
     * Constructs a GCMParameterSpec using the specified authentication
     * tag bit-length and IV buffer.
     *
     * @param src  the IV source buffer.  The contents of the buffer are
     *             copied to protect against subsequent modification.
     * @throws IllegalArgumentException if {@code src} is null.
     */
    public DefaultGCMParamSpec(byte[] src) {
        super(96, src);
    }

    /**
     * Constructs a GCMParameterSpec object using the specified
     * authentication tag bit-length and a subset of the specified
     * buffer as the IV.
     *
     * @param src    the IV source buffer.  The contents of the
     *               buffer are copied to protect against subsequent modification.
     * @param offset the offset in {@code src} where the IV starts
     * @param len    the number of IV bytes
     * @throws IllegalArgumentException if {@code src} is null, {@code len} or {@code offset} is negative,
     *                                  or the sum of {@code offset} and {@code len} is greater than the
     *                                  length of the {@code src} byte array.
     */
    public DefaultGCMParamSpec(byte[] src, int offset, int len) {
        super(96, src, offset, len);
    }
}
