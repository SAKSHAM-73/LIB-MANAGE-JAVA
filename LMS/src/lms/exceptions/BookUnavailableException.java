package lms.exceptions;

public class BookUnavailableException extends LMSException {
    public BookUnavailableException(String title) {
        super("BOOK_UNAVAIL", "No copies available for: " + title);
    }
}
