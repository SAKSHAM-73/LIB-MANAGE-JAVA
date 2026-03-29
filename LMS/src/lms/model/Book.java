package lms.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;
    private double pricePerDay;

    public Book() {}

    public Book(String bookId, String title, String author,
                String genre, int totalCopies, double pricePerDay) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.pricePerDay = pricePerDay;
    }

    // ── Getters & Setters ──────────────────────────────────
    public String getBookId()             { return bookId; }
    public void setBookId(String bookId)  { this.bookId = bookId; }

    public String getTitle()              { return title; }
    public void setTitle(String title)    { this.title = title; }

    public String getAuthor()             { return author; }
    public void setAuthor(String author)  { this.author = author; }

    public String getGenre()              { return genre; }
    public void setGenre(String genre)    { this.genre = genre; }

    public int getTotalCopies()                        { return totalCopies; }
    public void setTotalCopies(int totalCopies)        { this.totalCopies = totalCopies; }

    public int getAvailableCopies()                    { return availableCopies; }
    public void setAvailableCopies(int availableCopies){ this.availableCopies = availableCopies; }

    public double getPricePerDay()                     { return pricePerDay; }
    public void setPricePerDay(double pricePerDay)     { this.pricePerDay = pricePerDay; }

    public boolean isAvailable() { return availableCopies > 0; }

    @Override
    public String toString() {
        return String.format(
            "Book{id='%s', title='%s', author='%s', genre='%s', " +
            "available=%d/%d, price=%.2f/day}",
            bookId, title, author, genre, availableCopies, totalCopies, pricePerDay
        );
    }
}
