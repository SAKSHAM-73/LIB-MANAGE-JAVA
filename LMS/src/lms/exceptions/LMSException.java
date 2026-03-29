package lms.exceptions;

// ── Base LMS Exception ──────────────────────────────────────
public class LMSException extends Exception {
    private final String errorCode;

    public LMSException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
