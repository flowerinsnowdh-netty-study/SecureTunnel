package online.flowerinsnow.securetunnel.object;

import java.security.PrivateKey;
import java.util.Objects;

public class ClientEncryptSession extends EncryptHandshakeSession {
    private PrivateKey clientPrivateKey;

    public ClientEncryptSession() {
    }

    public synchronized PrivateKey getClientPrivateKey() {
        return this.clientPrivateKey;
    }

    public synchronized void setClientPrivateKey(PrivateKey clientPrivateKey) {
        this.clientPrivateKey = clientPrivateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientEncryptSession that = (ClientEncryptSession) o;
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
        return ClientEncryptSession.class.getSimpleName() + "{" +
                "super=" + super.toString() +
                ", clientPrivateKey=" + this.clientPrivateKey +
                '}';
    }
}
