package online.flowerinsnow.securetunnel.exception;

public class UnknownPacketException extends Exception {
    public UnknownPacketException() {
        super();
    }

    public UnknownPacketException(String message) {
        super(message);
    }

    public UnknownPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPacketException(Throwable cause) {
        super(cause);
    }
}
