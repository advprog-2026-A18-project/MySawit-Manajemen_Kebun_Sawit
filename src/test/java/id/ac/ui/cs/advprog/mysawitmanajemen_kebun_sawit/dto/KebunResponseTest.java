package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KebunResponseTest {

    @Test
    void testConstructor() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");

        KebunResponse response = new KebunResponse(
            "KB001",
            "Kebun Makmur",
            500,
            "{}",
            "M001",
            null,
            supirIds
        );

        assertEquals("KB001", response.getKodeKebun());
        assertEquals("Kebun Makmur", response.getNamaKebun());
        assertEquals(500, response.getLuasHektare());
        assertEquals("{}", response.getKoordinat());
        assertEquals("M001", response.getMandorId());
        assertEquals(1, response.getSupirIds().size());
    }

    @Test
    void testSetters() {
        KebunResponse response = new KebunResponse();

        response.setKodeKebun("KB001");
        response.setNamaKebun("Kebun Makmur");
        response.setLuasHektare(500);
        response.setKoordinat("{}");
        response.setMandorId("M001");
        response.setSupirIds(List.of("SUP001"));

        assertEquals("KB001", response.getKodeKebun());
        assertEquals("Kebun Makmur", response.getNamaKebun());
        assertEquals(500, response.getLuasHektare());
        assertEquals("M001", response.getMandorId());
    }

    @Test
    void testNullSupirIds() {
        KebunResponse response = new KebunResponse();
        response.setSupirIds(null);

        assertNull(response.getSupirIds());
    }
}