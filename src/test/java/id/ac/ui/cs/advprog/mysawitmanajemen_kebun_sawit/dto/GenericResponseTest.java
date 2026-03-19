package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericResponseTest {

    @Test
    void testSuccess_Returns200() {
        String message = "Success";
        Object data = "test data";

        GenericResponse<?> result = GenericResponse.success(message, data);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    void testError_ReturnsErrorCode() {
        int statusCode = 400;
        String message = "Error occurred";

        GenericResponse<?> result = GenericResponse.error(statusCode, message);

        assertNotNull(result);
        assertEquals(statusCode, result.getStatusCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }
}
