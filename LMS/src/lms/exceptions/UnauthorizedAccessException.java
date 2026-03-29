package lms.exceptions;

public class UnauthorizedAccessException extends LMSException {
    public UnauthorizedAccessException() {
        super("UNAUTHORIZED", "Access denied. Admin privileges required.");
    }
}
