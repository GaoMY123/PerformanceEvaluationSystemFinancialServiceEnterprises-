package com.performance.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private static final String SECRET = "01234567890123456789012345678901";
    private static final long EXPIRATION_MILLIS = 86_400_000L;

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = createProvider(SECRET, EXPIRATION_MILLIS);
    }

    @Test
    void generateTokenProducesValidTokenWithSubject() {
        String token = provider.generateToken("alice");

        assertTrue(provider.validateToken(token));
        assertEquals("alice", provider.getUsernameFromToken(token));
    }

    @Test
    void rejectsMalformedToken() {
        assertFalse(provider.validateToken("not-a-jwt"));
    }

    @Test
    void rejectsNullAndEmptyToken() {
        assertFalse(provider.validateToken(null));
        assertFalse(provider.validateToken(""));
    }

    @Test
    void rejectsExpiredToken() {
        JwtTokenProvider expiredProvider = createProvider(SECRET, -1_000L);
        String token = expiredProvider.generateToken("alice");

        assertFalse(provider.validateToken(token));
    }

    @Test
    void rejectsTokenSignedWithDifferentKey() {
        JwtTokenProvider otherProvider = createProvider("abcdefghijklmnopqrstuvwxyz123456", EXPIRATION_MILLIS);
        String token = otherProvider.generateToken("alice");

        assertFalse(provider.validateToken(token));
    }

    private JwtTokenProvider createProvider(String secret, long expirationMillis) {
        JwtTokenProvider created = new JwtTokenProvider();
        ReflectionTestUtils.setField(created, "secret", secret);
        ReflectionTestUtils.setField(created, "expiration", expirationMillis);
        created.init();
        return created;
    }
}
