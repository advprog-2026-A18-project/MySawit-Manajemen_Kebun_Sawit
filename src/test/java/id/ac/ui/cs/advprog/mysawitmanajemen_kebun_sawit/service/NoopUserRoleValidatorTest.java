package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoopUserRoleValidatorTest {

    private final NoopUserRoleValidator validator = new NoopUserRoleValidator();

    @Test
    void isValidRole_ReturnsTrue_WhenBothValid() {
        UUID userId = UUID.randomUUID();
        assertTrue(validator.isValidRole(userId, "MANDOR"));
    }

    @Test
    void isValidRole_ReturnsFalse_WhenUserIdIsNull() {
        assertFalse(validator.isValidRole(null, "MANDOR"));
    }

    @Test
    void isValidRole_ReturnsFalse_WhenExpectedRoleIsNull() {
        UUID userId = UUID.randomUUID();
        assertFalse(validator.isValidRole(userId, null));
    }

    @Test
    void isValidRole_ReturnsFalse_WhenExpectedRoleIsBlank() {
        UUID userId = UUID.randomUUID();
        assertFalse(validator.isValidRole(userId, "   "));
    }

    @Test
    void isValidRole_ReturnsFalse_WhenBothAreNull() {
        assertFalse(validator.isValidRole(null, null));
    }

    @Test
    void getFullname_ReturnsNull_Always() {
        assertNull(validator.getFullname(UUID.randomUUID()));
        assertNull(validator.getFullname(null));
    }
}