package online.flowerinsnow.securetunnel.exception;

public class GCMParameterNotInitializedException extends RuntimeException {
    public GCMParameterNotInitializedException() {
        super();
    }

    public GCMParameterNotInitializedException(String message) {
        super(message);
    }

    public GCMParameterNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GCMParameterNotInitializedException(Throwable cause) {
        super(cause);
    }
}
