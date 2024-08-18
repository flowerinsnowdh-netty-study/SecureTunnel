package online.flowerinsnow.securetunnel.object;

import java.security.PrivateKey;
import java.util.Objects;

public class ServerEncryptSession extends EncryptHandshakeSession {
    private PrivateKey serverPrivateKey;

    public ServerEncryptSession() {
    }

    public synchronized PrivateKey getServerPrivateKey() {
        return this.serverPrivateKey;
    }

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
