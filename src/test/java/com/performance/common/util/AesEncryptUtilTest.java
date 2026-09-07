package com.performance.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AesEncryptUtilTest {

    private static final String CUSTOM_KEY = "1234567890123456";

    @Test
    void encryptAndDecryptRoundTripUsesDefaultKey() {
        String plainText = "salary:12345.67";

        String cipherText = AesEncryptUtil.encrypt(plainText);

        assertNotEquals(plainText, cipherText);
        assertEquals(plainText, AesEncryptUtil.decrypt(cipherText));
    }

    @Test
    void encryptAndDecryptRoundTripUsesCustomKey() {
        String plainText = "bonus:8000.00";

        String cipherText = AesEncryptUtil.encrypt(plainText, CUSTOM_KEY);

        assertEquals(plainText, AesEncryptUtil.decrypt(cipherText, CUSTOM_KEY));
    }

    @Test
    void prefixMethodsMarkAndDecryptSensitiveValues() {
        String plainText = "base:15000";

        String stored = AesEncryptUtil.encryptWithPrefix(plainText);

        assertTrue(stored.startsWith("ENC:"));
        assertTrue(AesEncryptUtil.isEncrypted(stored));
        assertEquals(plainText, AesEncryptUtil.decryptWithPrefix(stored));
    }

    @Test
    void decryptWithPrefixKeepsNonEncryptedValueUnchanged() {
        assertEquals("15000", AesEncryptUtil.decryptWithPrefix("15000"));
        assertEquals(null, AesEncryptUtil.decryptWithPrefix(null));
    }

    @Test
    void isEncryptedOnlyRecognizesEnCPrefix() {
        assertFalse(AesEncryptUtil.isEncrypted(null));
        assertFalse(AesEncryptUtil.isEncrypted("plain"));
        assertTrue(AesEncryptUtil.isEncrypted("ENC:abc"));
    }

    @Test
    void decryptInvalidBase64ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> AesEncryptUtil.decrypt("not-base64!"));
    }
}
