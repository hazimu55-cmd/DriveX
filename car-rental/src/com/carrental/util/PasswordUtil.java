package com.carrental.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing using SHA-256 + salt.
 * Demonstrates Utility / Helper pattern in Core Java OOP.
 */
public final class PasswordUtil {

    private PasswordUtil() {} // Prevent instantiation — pure utility class

    public static String hashPassword(String plainPassword) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedBytes = md.digest(plainPassword.getBytes());

            // Encode salt + hash together: "salt$hash"
            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hashedBytes);
            return saltB64 + "$" + hashB64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedBytes = md.digest(plainPassword.getBytes());
            String hashB64 = Base64.getEncoder().encodeToString(hashedBytes);

            return hashB64.equals(parts[1]);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
}
