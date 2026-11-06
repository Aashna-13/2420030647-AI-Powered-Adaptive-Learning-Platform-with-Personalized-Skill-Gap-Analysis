package com.skillbridge.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        // Use a test secret >= 256 bits
        String testSecret = "SkillBridgeTestSecretKeyMustBeAtLeast256BitsLongForHMACSHA256ComplianceInUnitTests!";
        jwtUtils = new JwtUtils(testSecret, 3600000L); // 1 hour
    }

    @Test
    @DisplayName("Generate and validate valid JWT token")
    void testGenerateAndValidateToken() {
        String token = jwtUtils.generateToken("student@skillbridge.ai", "STUDENT", "Demo Student", 1L);

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token), "Valid JWT must pass validation");
        assertEquals("student@skillbridge.ai", jwtUtils.getEmailFromToken(token));
        assertEquals("STUDENT", jwtUtils.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Expired JWT token is rejected")
    void testExpiredToken() {
        String expiredToken = jwtUtils.generateExpiredTokenForTesting("student@skillbridge.ai", "STUDENT");

        assertNotNull(expiredToken);
        assertFalse(jwtUtils.validateToken(expiredToken), "Expired token must be rejected");
    }

    @Test
    @DisplayName("Malformed or tampered token is rejected")
    void testMalformedToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.structure"));
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken(null));
    }
}
