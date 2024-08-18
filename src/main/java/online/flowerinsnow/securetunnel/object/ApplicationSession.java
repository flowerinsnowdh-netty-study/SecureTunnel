package online.flowerinsnow.securetunnel.object;

import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;

/**
 * <p>通信会话</p>
 * <p>线程安全类</p>
 */
public class ApplicationSession {
    /**
     * <p>客户端临时公钥</p>
     */
    protected PublicKey clientPublicKey;
    /**
     * <p>服务器临时公钥</p>
     */
    protected PublicKey serverPublicKey;

    /**
     * <p>共享安全密钥</p>
     */
    protected SecretKeySpec sharedSecretKey;
    /**
     * <p>客户端发往服务器的 GCM 参数</p>
     */
    protected byte[] c2sGCMParameter;
    /**
     * <p>服务器发往客户端的 GCM 参数</p>
     */
    protected byte[] s2cGCMParameter;

    /**
     * <p>封锁构造方法，只允许从子类实例化</p>
     */
    protected ApplicationSession() {
    }

    /**
     * <p>获取客户端临时公钥</p>
     *
     * @return 客户端临时公钥
     */
    public synchronized PublicKey getClientPublicKey() {
        return this.clientPublicKey;
    }

    /**
     * <p>设置客户端临时公钥</p>
     *
     * @param clientPublicKey 客户端临时公钥
     */
    public synchronized void setClientPublicKey(PublicKey clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    /**
     * <p>获取服务器临时公钥</p>
     *
     * @return 服务器临时公钥
     */
    public synchronized PublicKey getServerPublicKey() {
        return this.serverPublicKey;
    }

    /**
     * <p>设置服务器临时公钥</p>
     *
     * @param serverPublicKey 服务器临时公钥
     */
    public synchronized void setServerPublicKey(PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    /**
     * <p>获取共享安全密钥</p>
     *
     * @return 共享安全密钥
     */
    public synchronized SecretKeySpec getSharedSecretKey() {
        return this.sharedSecretKey;
    }

    /**
     * <p>设置共享安全密钥</p>
     *
     * @param sharedSecretKey 共享安全密钥
     */
    public synchronized void setSharedSecretKey(SecretKeySpec sharedSecretKey) {
        this.sharedSecretKey = sharedSecretKey;
    }

    /**
     * <p>获取客户端发往服务器的 GCM 参数</p>
     * @return 客户端发往服务器的 GCM 参数
     */
    public synchronized byte[] getC2SGCMParameter() {
        return this.c2sGCMParameter;
    }

    /**
     * 设置客户端发往服务器的 GCM 参数
     *
     * @param c2sGCMParameter 客户端发往服务器的 GCM 参数
     */
    public synchronized void setC2SGCMParameter(byte[] c2sGCMParameter) {
        this.c2sGCMParameter = c2sGCMParameter;
    }

    /**
     * <p>获取服务器发往客户端的 GCM 参数</p>
     *
     * @return 服务器发往客户端的 GCM 参数
     */
    public synchronized byte[] getS2CGCMParameter() {
        return this.s2cGCMParameter;
    }

    /**
     * <p>设置服务器发往客户端的 GCM 参数</p>
     * @param s2cGCMParameter 服务器发往客户端的 GCM 参数
     */
    public synchronized void setS2CGCMParameter(byte[] s2cGCMParameter) {
        this.s2cGCMParameter = s2cGCMParameter;
    }

    /**
     * <p>更新客户端发往服务器的 GCM 参数</p>
     */
    public synchronized void updateC2SGCMParameter() {
        this.c2sGCMParameter = CipherUtils.SM3.sm3(this.c2sGCMParameter);
    }

    /**
     * <p>更新服务器发往客户端的 GCM 参数</p>
     */
    public synchronized void updateS2CGCMParameter() {
        this.s2cGCMParameter = CipherUtils.SM3.sm3(this.s2cGCMParameter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationSession that = (ApplicationSession) o;
        return Objects.equals(this.clientPublicKey, that.clientPublicKey) && Objects.equals(this.serverPublicKey, that.serverPublicKey) && Objects.equals(this.sharedSecretKey, that.sharedSecretKey) && Arrays.equals(this.c2sGCMParameter, that.c2sGCMParameter) && Arrays.equals(this.s2cGCMParameter, that.s2cGCMParameter);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.clientPublicKey != null ? this.clientPublicKey.hashCode() : 0);
        result = 31 * result + (this.serverPublicKey != null ? this.serverPublicKey.hashCode() : 0);
        result = 31 * result + (this.sharedSecretKey != null ? this.sharedSecretKey.hashCode() : 0);
        result = 31 * result + (this.c2sGCMParameter != null ? Arrays.hashCode(this.c2sGCMParameter) : 0);
        result = 31 * result + (this.s2cGCMParameter != null ? Arrays.hashCode(this.s2cGCMParameter) : 0);
        return result;
    }

    @Override
    public String toString() {
        return ApplicationSession.class.getSimpleName() + "{" +
                "clientPublicKey=" + this.clientPublicKey +
                ", serverPublicKey=" + this.serverPublicKey +
                ", sharedSecretKey=" + this.sharedSecretKey +
                ", c2sGCMParameter=" + Arrays.toString(this.c2sGCMParameter) +
                ", s2cGCMParameter=" + Arrays.toString(this.s2cGCMParameter) +
                '}';
    }
}
