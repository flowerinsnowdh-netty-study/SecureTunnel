package online.flowerinsnow.securetunnel.object;

import java.security.PrivateKey;
import java.util.Objects;

/**
 * <p>客户端通信会话</p>
 */
public class ClientSession extends ApplicationSession {
    /**
     * <p>客户端临时私钥，客户端独有</p>
     */
    private PrivateKey clientPrivateKey;

    /**
     * <p>开放构造方法，允许实例化</p>
     */
    public ClientSession() {
    }

    /**
     * <p>获取客户端临时私钥</p>
     *
     * @return 客户端临时私钥
     */
    public synchronized PrivateKey getClientPrivateKey() {
        return this.clientPrivateKey;
    }

    /**
     * <p>设置客户端临时私钥</p>
     * @param clientPrivateKey 客户端临时私钥
     */
    public synchronized void setClientPrivateKey(PrivateKey clientPrivateKey) {
        this.clientPrivateKey = clientPrivateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientSession that = (ClientSession) o;
        return Objects.equals(this.clientPrivateKey, that.clientPrivateKey);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + super.hashCode();
        result = 31 * result + (this.clientPrivateKey != null ? this.clientPrivateKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ClientSession.class.getSimpleName() + "{" +
                "super=" + super.toString() +
                ", clientPrivateKey=" + this.clientPrivateKey +
                '}';
    }
}
