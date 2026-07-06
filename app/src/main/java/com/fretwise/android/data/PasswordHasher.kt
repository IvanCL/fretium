package com.fretwise.android.data

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * PBKDF2-HMAC-SHA256 password hashing (JDK-only, no external native dependency).
 * Stored format: "iterations:saltBase64:hashBase64".
 */
object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password, salt, ITERATIONS)
        return "$ITERATIONS:${encode(salt)}:${encode(hash)}"
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 3) return false
        val iterations = parts[0].toIntOrNull() ?: return false
        val salt = decode(parts[1])
        val expected = decode(parts[2])
        val actual = pbkdf2(password, salt, iterations)
        return actual.contentEquals(expected)
    }

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    private fun encode(bytes: ByteArray): String =
        android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

    private fun decode(str: String): ByteArray =
        android.util.Base64.decode(str, android.util.Base64.NO_WRAP)
}
