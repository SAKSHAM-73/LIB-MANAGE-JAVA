package lms.model;

import java.time.LocalDate;

public class BorrowRecord {
    private int borrowId;
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private String status;          // "ACTIVE" | "RETURNED"

    public BorrowRecord() {}

    public BorrowRecord(String userId, String bookId,
                        LocalDate borrowDate, LocalDate dueDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "ACTIVE";
        this.fineAmount = 0.0;
    }

    // ── Getters & Setters ──────────────────────────────────
    public int getBorrowId()                          { return borrowId; }
    public void setBorrowId(int borrowId)             { this.borrowId = borrowId; }

    public String getUserId()                         { return userId; }
    public void setUserId(String userId)              { this.userId = userId; }

    public String getBookId()                         { return bookId; }
    public void setBookId(String bookId)              { this.bookId = bookId; }

    public LocalDate getBorrowDate()                  { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate)   { this.borrowDate = borrowDate; }

    public LocalDate getDueDate()                     { return dueDate; }
    public void setDueDate(LocalDate dueDate)         { this.dueDate = dueDate; }

    public LocalDate getReturnDate()                  { return returnDate; }
    public void setReturnDate(LocalDate returnDate)   { this.returnDate = returnDate; }

    public double getFineAmount()                     { return fineAmount; }
    public void setFineAmount(double fineAmount)      { this.fineAmount = fineAmount; }

    public String getStatus()                         { return status; }
    public void setStatus(String status)              { this.status = status; }

    @Override
    public String toString() {
        return String.format(
            "BorrowRecord{id=%d, userId='%s', bookId='%s', " +
            "borrowed=%s, due=%s, status=%s, fine=%.2f}",
            borrowId, userId, bookId, borrowDate, dueDate, status, fineAmount
        );
    }
}
