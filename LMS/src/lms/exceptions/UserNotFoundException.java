package lms.exceptions;

public class UserNotFoundException extends LMSException {
    public UserNotFoundException(String userId) {
        super("USER_404", "User not found with ID: " + userId);
    }
}
