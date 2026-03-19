package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KebunTest {

    private Kebun kebun;

    @BeforeEach
    void setUp() {
        kebun = new Kebun();
    }

    @Test
    void testPrePersist_SetsCreatedAt() {
        OffsetDateTime before = OffsetDateTime.now();

        kebun.prePersist();

        assertNotNull(kebun.getCreatedAt());
        assertTrue(kebun.getCreatedAt().isEqual(before) || kebun.getCreatedAt().isAfter(before));
    }

    @Test
    void testPrePersist_DoesNotOverwriteExistingCreatedAt() {
        OffsetDateTime existingTime = OffsetDateTime.now().minusDays(1);
        kebun.setCreatedAt(existingTime);

        kebun.prePersist();

        assertEquals(existingTime, kebun.getCreatedAt());
    }

    @Test
    void testSupirIds_Initialization() {
        assertNotNull(kebun.getSupirIds());
        assertTrue(kebun.getSupirIds().isEmpty());
    }

    @Test
    void testSetSupirIds() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");
        supirIds.add("SUP002");

        kebun.setSupirIds(supirIds);

        assertEquals(2, kebun.getSupirIds().size());
        assertTrue(kebun.getSupirIds().contains("SUP001"));
        assertTrue(kebun.getSupirIds().contains("SUP002"));
    }

    @Test
    void testSetterGetter() {
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");
        kebun.setMandorId("M001");

        assertEquals("KB001", kebun.getKodeKebun());
        assertEquals("Kebun Makmur", kebun.getNamaKebun());
        assertEquals(500, kebun.getLuasHektare());
        assertEquals("{}", kebun.getKoordinat());
        assertEquals("M001", kebun.getMandorId());
    }
}
