package com.pvncodes.MD.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "0123456789-0123456789-0123456789-ab";

    @Test
    void validTokenRoundTripsIdAndRole() {
        JwtService svc = new JwtService(SECRET, 60_000);
        String token = svc.generateToken(42L, "NGO");

        Optional<Claims> claims = svc.parse(token);

        assertTrue(claims.isPresent());
        assertEquals("42", claims.get().getSubject());
        assertEquals("NGO", claims.get().get("role", String.class));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService svc = new JwtService(SECRET, -1_000);
        assertTrue(svc.parse(svc.generateToken(1L, "USER")).isEmpty());
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String token = new JwtService(SECRET, 60_000).generateToken(1L, "USER");
        JwtService other = new JwtService("another-secret-another-secret-another-1", 60_000);
        assertTrue(other.parse(token).isEmpty());
    }

    @Test
    void legacyFakeTokenIsRejected() {
        JwtService svc = new JwtService(SECRET, 60_000);
        assertTrue(svc.parse("fake-jwt-token-1").isEmpty());
        assertTrue(svc.parse("ngo-token-1").isEmpty());
    }

    @Test
    void tamperedPayloadIsRejected() {
        JwtService svc = new JwtService(SECRET, 60_000);
        String[] parts = svc.generateToken(1L, "USER").split("\\.");
        String forged = parts[0] + "." + parts[1] + "x." + parts[2];
        assertTrue(svc.parse(forged).isEmpty());
    }
}
