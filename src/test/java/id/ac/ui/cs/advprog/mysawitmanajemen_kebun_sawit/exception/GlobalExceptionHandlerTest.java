package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.exception;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleKebunNotFound() {
        KebunNotFoundException ex = new KebunNotFoundException("KB999");

        GenericResponse<Void> result = handler.handleKebunNotFound(ex);

        assertEquals(404, result.getStatusCode());
        assertTrue(result.getMessage().contains("KB999"));
        assertNull(result.getData());
    }

    @Test
    void testHandleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Cannot delete");

        GenericResponse<Void> result = handler.handleIllegalState(ex);

        assertEquals(400, result.getStatusCode());
        assertEquals("Cannot delete", result.getMessage());
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid");

        GenericResponse<Void> result = handler.handleIllegalArgument(ex);

        assertEquals(400, result.getStatusCode());
        assertEquals("Invalid", result.getMessage());
    }
}
