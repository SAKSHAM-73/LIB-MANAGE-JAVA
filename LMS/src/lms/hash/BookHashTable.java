package lms.hash;

import lms.model.Book;

/**
 * Custom Hash Table for in-memory Book lookup.
 *
 * HASHING STRATEGY:
 *   - Hash function  : sum of char values of bookId × 31, then mod TABLE_SIZE
 *   - Collision fix  : Linear Probing (search next slot until empty)
 *
 * This gives O(1) average lookup vs O(n) linear scan over a list.
 */
public class BookHashTable {

    private static final int TABLE_SIZE = 101;   // prime number reduces clustering

    private final Book[] table;
    private int size;

    public BookHashTable() {
        table = new Book[TABLE_SIZE];
        size = 0;
    }

    // ── HASH FUNCTION ───────────────────────────────────────
    private int hash(String key) {
        int h = 0;
        for (char c : key.toCharArray()) {
            h = (h * 31 + c) % TABLE_SIZE;
        }
        return Math.abs(h);
    }

    // ── INSERT ──────────────────────────────────────────────
    /**
     * Insert or update a book in the hash table.
     * Collision resolved via LINEAR PROBING: if slot is taken,
     * move to (index + 1) % TABLE_SIZE until an empty slot is found.
     */
    public void put(Book book) {
        int index = hash(book.getBookId());

        // Linear probing
        while (table[index] != null &&
               !table[index].getBookId().equals(book.getBookId())) {
            index = (index + 1) % TABLE_SIZE;   // ← conflict resolution
        }

        if (table[index] == null) size++;
        table[index] = book;
    }

    // ── GET ─────────────────────────────────────────────────
    public Book get(String bookId) {
        int index = hash(bookId);
        int start = index;

        while (table[index] != null) {
            if (table[index].getBookId().equals(bookId)) {
                return table[index];
            }
            index = (index + 1) % TABLE_SIZE;
            if (index == start) break;   // full loop — not found
        }
        return null;
    }

    // ── REMOVE ──────────────────────────────────────────────
    public void remove(String bookId) {
        int index = hash(bookId);
        int start = index;

        while (table[index] != null) {
            if (table[index].getBookId().equals(bookId)) {
                table[index] = null;
                size--;
                return;
            }
            index = (index + 1) % TABLE_SIZE;
            if (index == start) break;
        }
    }

    // ── CONTAINS ────────────────────────────────────────────
    public boolean contains(String bookId) {
        return get(bookId) != null;
    }

    public int size() { return size; }

    // ── DEBUG DUMP ──────────────────────────────────────────
    public void printTable() {
        System.out.println("=== BookHashTable (size=" + size + ") ===");
        for (int i = 0; i < TABLE_SIZE; i++) {
            if (table[i] != null) {
                System.out.printf("  [%3d] hash(%s)=%d → %s%n",
                    i, table[i].getBookId(), hash(table[i].getBookId()), table[i].getTitle());
            }
        }
    }
}
