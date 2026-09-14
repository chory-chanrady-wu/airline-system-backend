package com.example.backend.service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public final class PasswordHasher {
    private PasswordHasher() {
    }
    public static String sha256(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash password with SHA-256", ex);
        }
    }
    public static boolean matches(String rawPassword, String expectedHash) {
        return sha256(rawPassword).equalsIgnoreCase(expectedHash);
    }
}