package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final String SECRET =
            "testSecretForTesting12345678901234567890";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 600000);
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        String token = generateToken("user@test.com", "MANDOR");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void testValidateToken_ExpiredToken_ReturnsFalse() {
        String token = generateExpiredToken("user@test.com", "BURUH");
        assertFalse(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_NullToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken(null));
    }

    @Test
    void testGetEmailFromToken() {
        String token = generateToken("user@test.com", "SUPIR");
        assertEquals("user@test.com", jwtUtils.getEmailFromToken(token));
    }

    @Test
    void testGetRoleFromToken() {
        String token = generateToken("user@test.com", "ADMIN");
        assertEquals("ADMIN", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void testValidateToken_EmptyString_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken(""));
    }

    private String generateToken(String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(key)
                .compact();
    }

    private String generateExpiredToken(String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis() - 1200000))
                .expiration(new Date(System.currentTimeMillis() - 600000))
                .signWith(key)
                .compact();
    }
}
