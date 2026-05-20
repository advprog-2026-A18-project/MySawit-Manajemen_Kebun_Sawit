package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoUtilsTest {

    private static final String VALID_RECT =
            "[{\"lat\":0,\"lng\":0},{\"lat\":0,\"lng\":100},"
                    + "{\"lat\":100,\"lng\":100},{\"lat\":100,\"lng\":0}]";

    @Test
    void testValidateKoordinat_ValidRectangle_NoException() {
        assertDoesNotThrow(() -> GeoUtils.validateKoordinat(VALID_RECT));
    }

    @Test
    void testValidateKoordinat_Null_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> GeoUtils.validateKoordinat(null));
    }

    @Test
    void testValidateKoordinat_Blank_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> GeoUtils.validateKoordinat("   "));
    }

    @Test
    void testValidateKoordinat_Not4Points_ThrowsException() {
        String twoPoints = "[{\"lat\":0,\"lng\":0},{\"lat\":100,\"lng\":100}]";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> GeoUtils.validateKoordinat(twoPoints));
        assertTrue(ex.getMessage().contains("exactly 4"));
    }

    @Test
    void testValidateKoordinat_NotRectangle_ThrowsException() {
        String irregular = "[{\"lat\":0,\"lng\":0},{\"lat\":0,\"lng\":100},"
                + "{\"lat\":50,\"lng\":150},{\"lat\":100,\"lng\":0}]";
        assertThrows(IllegalArgumentException.class,
                () -> GeoUtils.validateKoordinat(irregular));
    }

    @Test
    void testIsOverlapping_Overlapping_ReturnsTrue() {
        String coord1 = "[{\"lat\":0,\"lng\":0},{\"lat\":0,\"lng\":200},"
                + "{\"lat\":200,\"lng\":200},{\"lat\":200,\"lng\":0}]";
        String coord2 = "[{\"lat\":50,\"lng\":50},{\"lat\":50,\"lng\":150},"
                + "{\"lat\":150,\"lng\":150},{\"lat\":150,\"lng\":50}]";
        assertTrue(GeoUtils.isOverlapping(coord1, coord2));
    }

    @Test
    void testIsOverlapping_NotOverlapping_ReturnsFalse() {
        String coord1 = "[{\"lat\":0,\"lng\":0},{\"lat\":0,\"lng\":100},"
                + "{\"lat\":100,\"lng\":100},{\"lat\":100,\"lng\":0}]";
        String coord2 = "[{\"lat\":200,\"lng\":200},{\"lat\":200,\"lng\":300},"
                + "{\"lat\":300,\"lng\":300},{\"lat\":300,\"lng\":200}]";
        assertFalse(GeoUtils.isOverlapping(coord1, coord2));
    }

    @Test
    void testIsOverlapping_NullCoord1_ReturnsFalse() {
        assertFalse(GeoUtils.isOverlapping(null, VALID_RECT));
    }

    @Test
    void testIsOverlapping_NullCoord2_ReturnsFalse() {
        assertFalse(GeoUtils.isOverlapping(VALID_RECT, null));
    }

    @Test
    void testIsOverlapping_EmptyCoord_ReturnsFalse() {
        assertFalse(GeoUtils.isOverlapping("[]", VALID_RECT));
    }

    @Test
    void testParsePoints_ValidJson_Returns4Points() {
        List<double[]> points = GeoUtils.parsePoints(VALID_RECT);
        assertEquals(4, points.size());
    }

    @Test
    void testParsePoints_EmptyJson_ReturnsEmptyList() {
        assertTrue(GeoUtils.parsePoints("[]").isEmpty());
    }

    @Test
    void testComputeBoundingBox_ValidCoord() {
        double[] bbox = GeoUtils.computeBoundingBox(VALID_RECT);
        assertNotNull(bbox);
        assertEquals(0, bbox[0]);   // minLat
        assertEquals(100, bbox[1]); // maxLat
        assertEquals(0, bbox[2]);   // minLng
        assertEquals(100, bbox[3]); // maxLng
    }

    @Test
    void testComputeBoundingBox_EmptyCoord_ReturnsNull() {
        assertNull(GeoUtils.computeBoundingBox("[]"));
    }

    @Test
    void testValidateKoordinat_Square_NoException() {
        String square = "[{\"lat\":10,\"lng\":10},{\"lat\":10,\"lng\":20},"
                + "{\"lat\":20,\"lng\":20},{\"lat\":20,\"lng\":10}]";
        assertDoesNotThrow(() -> GeoUtils.validateKoordinat(square));
    }
}
