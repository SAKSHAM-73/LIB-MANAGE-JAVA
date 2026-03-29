package lms.exceptions;

public class InvalidCredentialsException extends LMSException {
    public InvalidCredentialsException() {
        super("AUTH_FAIL", "Invalid email or password.");
    }
}
