package cn.cqupt.util;

/**
 * Created by Cbillow on 15/10/27.
 */
public class CPException extends RuntimeException {

    public CPException() {
        super();
    }

    public CPException(String message) {
        super(message);
    }

    public CPException(String message, Throwable cause) {
        super(message, cause);
    }

    public CPException(Throwable cause) {
        super(cause);
    }
}
