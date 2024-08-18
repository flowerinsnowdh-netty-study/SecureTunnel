package online.flowerinsnow.securetunnel.exception;

public class MagicNumberMismatchException extends Exception {
    public MagicNumberMismatchException() {
        super();
    }

    public MagicNumberMismatchException(String message) {
        super(message);
    }

    public MagicNumberMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public MagicNumberMismatchException(Throwable cause) {
        super(cause);
    }
}
