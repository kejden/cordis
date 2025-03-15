package io.ndk.cordis_backend.serviceTests;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.ndk.cordis_backend.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class JwtServiceTests {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        this.jwtService = new JwtServiceImpl();
    }

    private String generateTokenWithCustomExpiration(String username, long offsetMillis) {
        String base64Key = "FKuIW2kY4nNmiFlXQzTOS6qwCvRQ5FZHoxDpiJNBKcRYAJaZnhHHP7AjVQV0cG0a";

        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        Date now = new Date(System.currentTimeMillis());
        Date expires = new Date(System.currentTimeMillis() + offsetMillis);

        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expires)
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes))
                .compact();
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token, "Generated token should not be null");
        assertTrue(token.split("\\.").length == 3,
                "Generated token should contain 3 parts separated by '.'");
    }

    @Test
    void testExtractUserName() {
        String username = "someUser";
        String token = jwtService.generateToken(username);
        String extracted = jwtService.extractUserName(token);
        assertEquals(username, extracted, "Should extract the same username from the token");
    }

    @Test
    void testValidateToken_Success() {
        String username = "validUser";
        String token = jwtService.generateToken(username);

        UserDetails userDetails = new User(username, "password", new ArrayList<>());
        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid, "Token should be valid for the matching user details");
    }

    @Test
    void testValidateToken_WrongUserName() {
        String token = jwtService.generateToken("someoneElse");
        UserDetails otherUser = new User("differentUser", "password", new ArrayList<>());
        assertFalse(jwtService.validateToken(token, otherUser),
                "Validation must fail if the token's subject doesn't match the user details");
    }

    @Test
    void testValidateExpiredToken() {
        String expiredToken = generateTokenWithCustomExpiration("expiredUser", -1000);
        UserDetails userDetails = new User("expiredUser", "password", new ArrayList<>());

        ExpiredJwtException ex = assertThrows(
                ExpiredJwtException.class,
                () ->jwtService.validateToken(expiredToken, userDetails)
        );

        assertTrue(ex.getMessage().contains("JWT expired"));
    }

    @Test
    void testValidateJwtToken_valid() {
        String token = jwtService.generateToken("validUser");
        boolean result = jwtService.validateJwtToken(token);
        assertTrue(result, "validateJwtToken should return true for a valid token");
    }

    @Test
    void testValidateJwtToken_malformed() {
        String errorMessage = "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.";
        String token = jwtService.generateToken("testUser");
        String malformed = token.substring(0, token.length() - 3);

        SignatureException ex = assertThrows(SignatureException.class, () -> jwtService.validateJwtToken(malformed));
        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testValidateJwtToken_expired() {
        String token = generateTokenWithCustomExpiration("expiredUser", -1000);
        boolean result = jwtService.validateJwtToken(token);
        assertFalse(result, "Expired token should fail validation with validateJwtToken()");
    }

    @Test
    void testValidateJwtToken_unsupported() {
        String unsupportedToken = "abc.def.ghi.jkl";
        boolean result = jwtService.validateJwtToken(unsupportedToken);
        assertFalse(result, "Unsupported token format should fail validation");
    }

    @Test
    void testValidateJwtToken_illegalArgument() {
        boolean result = jwtService.validateJwtToken("");
        assertFalse(result, "Blank token should fail validation due to illegal argument");
    }

    @Test
    void testParseJwt_ValidAuthorizationHeader() {
        StompHeaderAccessor accessor = Mockito.mock(StompHeaderAccessor.class);
        when(accessor.getFirstNativeHeader("Authorization")).thenReturn("Bearer valid.jwt.token");

        String result = jwtService.parseJwt(accessor);

        assertEquals("valid.jwt.token", result, "JWT should be extracted correctly from the Authorization header");
    }

    @Test
    void testParseJwt_MissingAuthorizationHeader() {
        StompHeaderAccessor accessor = Mockito.mock(StompHeaderAccessor.class);
        when(accessor.getFirstNativeHeader("Authorization")).thenReturn(null);

        String result = jwtService.parseJwt(accessor);

        assertNull(result, "Method should return null when Authorization header is missing");
    }
}
