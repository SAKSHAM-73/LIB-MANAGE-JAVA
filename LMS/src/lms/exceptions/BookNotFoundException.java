package lms.exceptions;

public class BookNotFoundException extends LMSException {
    public BookNotFoundException(String bookId) {
        super("BOOK_404", "Book not found with ID: " + bookId);
    }
}
