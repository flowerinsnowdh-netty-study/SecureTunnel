package online.flowerinsnow.securetunnel.object;

import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;

public class EncryptHandshakeSession {
    public final Object sharedSecretKeyLock = new Object();

    protected PublicKey clientPublicKey;
    protected PublicKey serverPublicKey;

    protected SecretKeySpec sharedSecretKey;
    protected byte[] c2sGCMParameter;
    protected byte[] s2cGCMParameter;

    protected EncryptHandshakeSession() {
    }

    public synchronized PublicKey getClientPublicKey() {
        return this.clientPublicKey;
    }

    public synchronized void setClientPublicKey(PublicKey clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public synchronized PublicKey getServerPublicKey() {
        return this.serverPublicKey;
    }

    public synchronized void setServerPublicKey(PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public synchronized SecretKeySpec getSharedSecretKey() {
        return this.sharedSecretKey;
    }

    public synchronized void setSharedSecretKey(SecretKeySpec sharedSecretKey) {
        this.sharedSecretKey = sharedSecretKey;
    }

    public synchronized byte[] getC2SGCMParameter() {
        return this.c2sGCMParameter;
    }

    public synchronized void setC2SGCMParameter(byte[] c2sGCMParameter) {
        this.c2sGCMParameter = c2sGCMParameter;
    }

    public synchronized byte[] getS2CGCMParameter() {
        return this.s2cGCMParameter;
    }

    public synchronized void setS2CGCMParameter(byte[] s2cGCMParameter) {
        this.s2cGCMParameter = s2cGCMParameter;
    }

    public synchronized void updateC2SGCMParameter() {
        this.c2sGCMParameter = CipherUtils.sm3(this.c2sGCMParameter);
    }

    public synchronized void updateS2CGCMParameter() {
        this.s2cGCMParameter = CipherUtils.sm3(this.s2cGCMParameter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptHandshakeSession that = (EncryptHandshakeSession) o;
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
        return EncryptHandshakeSession.class.getSimpleName() + "{" +
                "clientPublicKey=" + this.clientPublicKey +
                ", serverPublicKey=" + this.serverPublicKey +
                ", sharedSecretKey=" + this.sharedSecretKey +
                ", c2sGCMParameter=" + Arrays.toString(this.c2sGCMParameter) +
                ", s2cGCMParameter=" + Arrays.toString(this.s2cGCMParameter) +
                '}';
    }
}
