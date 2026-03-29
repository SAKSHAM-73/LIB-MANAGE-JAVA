package lms.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple SHA-256 password hashing utility.
 *
 * WHY SHA-256 HERE?
 * ------------------
 * This project focuses on demonstrating hashing concepts for an
 * academic LMS. SHA-256 is deterministic, collision-resistant
 * (for practical purposes), and produces a fixed 256-bit (64 hex char) output.
 *
 * Production systems should use bcrypt/Argon2 with salt,
 * but SHA-256 is sufficient to illustrate the concept.
 */
public class PasswordUtil {

    private PasswordUtil() {}  // Utility class — no instances

    /**
     * Hash a plain-text password to SHA-256 hex string.
     * @param plainText  raw password
     * @return           64-character lowercase hex digest
     */
    public static String hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JVM spec — should never happen
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    /**
     * Verify a plain-text password against a stored hash.
     */
    public static boolean verify(String plainText, String storedHash) {
        return hash(plainText).equals(storedHash);
    }

    // ── Helper ──────────────────────────────────────────────
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
