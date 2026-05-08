package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KebunResponseTest {

    @Test
    void testConstructor() {
        UUID mandorId = UUID.randomUUID();
        List<UUID> supirIds = new ArrayList<>();
        supirIds.add(UUID.randomUUID());
        List<String> listSupir = new ArrayList<>();
        listSupir.add("Supir A");

        KebunResponse response = new KebunResponse(
            "KB001",
            "Kebun Makmur",
            500,
            "{}",
            mandorId,
            "Pak Mandor",
            null,
            supirIds,
            listSupir
        );

        assertEquals("KB001", response.getKodeKebun());
        assertEquals("Kebun Makmur", response.getNamaKebun());
        assertEquals(500, response.getLuasHektare());
        assertEquals("{}", response.getKoordinat());
        assertEquals(mandorId, response.getMandorId());
        assertEquals("Pak Mandor", response.getNamaMandor());
        assertEquals(1, response.getSupirIds().size());
    }

    @Test
    void testSetters() {
        UUID mandorId = UUID.randomUUID();
        UUID supirId = UUID.randomUUID();
        KebunResponse response = new KebunResponse();

        response.setKodeKebun("KB001");
        response.setNamaKebun("Kebun Makmur");
        response.setLuasHektare(500);
        response.setKoordinat("{}");
        response.setMandorId(mandorId);
        response.setNamaMandor("Pak Mandor");
        response.setSupirIds(List.of(supirId));
        response.setListSupir(List.of("Supir A"));

        assertEquals("KB001", response.getKodeKebun());
        assertEquals("Kebun Makmur", response.getNamaKebun());
        assertEquals(500, response.getLuasHektare());
        assertEquals(mandorId, response.getMandorId());
        assertEquals("Pak Mandor", response.getNamaMandor());
    }

    @Test
    void testNullSupirIds() {
        KebunResponse response = new KebunResponse();
        response.setSupirIds(null);

        assertNull(response.getSupirIds());
    }
}