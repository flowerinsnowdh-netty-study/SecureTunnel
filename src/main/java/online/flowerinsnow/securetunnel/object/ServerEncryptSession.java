package online.flowerinsnow.securetunnel.object;

import java.security.PrivateKey;
import java.util.Objects;

/**
 * <p>服务器通信会话</p>
 */
public class ServerEncryptSession extends EncryptHandshakeSession {
    /**
     * <p>服务器临时私钥</p>
     * <p>服务器独有</p>
     */
    private PrivateKey serverPrivateKey;

    /**
     * <p>开放构造方法，允许实例化</p>
     */
    public ServerEncryptSession() {
    }

    /**
     * <p>获取服务器临时私钥</p>
     *
     * @return 服务器临时私钥
     */
    public synchronized PrivateKey getServerPrivateKey() {
        return this.serverPrivateKey;
    }

    /**
     * <p>设置服务器临时私钥</p>
     * @param serverPrivateKey 服务器临时私钥
     */
    public synchronized void setServerPrivateKey(PrivateKey serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServerEncryptSession that = (ServerEncryptSession) o;
        return Objects.equals(this.serverPrivateKey, that.serverPrivateKey);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + super.hashCode();
        result = 31 * result + (this.serverPrivateKey != null ? this.serverPrivateKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ServerEncryptSession.class.getSimpleName() + "{" +
                "super=" + super.toString() +
                ", serverPrivateKey=" + this.serverPrivateKey +
                '}';
    }
}
