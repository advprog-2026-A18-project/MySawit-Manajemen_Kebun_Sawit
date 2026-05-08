package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

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
    void testSupirList_Initialization() {
        assertNotNull(kebun.getSupirList());
        assertTrue(kebun.getSupirList().isEmpty());
    }

    @Test
    void testAddSupir() {
        UUID supirId = UUID.randomUUID();
        String supirNama = "Supir A";

        kebun.addSupir(supirId, supirNama);

        assertEquals(1, kebun.getSupirIds().size());
        assertEquals(supirId, kebun.getSupirIds().get(0));
        assertEquals(supirNama, kebun.getSupirNamas().get(0));
    }

    @Test
    void testRemoveSupir() {
        UUID supirId = UUID.randomUUID();
        String supirNama = "Supir A";

        kebun.addSupir(supirId, supirNama);
        assertEquals(1, kebun.getSupirIds().size());

        kebun.removeSupir(supirId);
        assertEquals(0, kebun.getSupirIds().size());
    }

    @Test
    void testSetterGetter() {
        UUID mandorId = UUID.randomUUID();
        String mandorNama = "Pak Mandor";
        UUID supirId = UUID.randomUUID();

        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");
        kebun.setMandorId(mandorId);
        kebun.setMandorNama(mandorNama);
        kebun.addSupir(supirId, "Supir A");

        assertEquals("KB001", kebun.getKodeKebun());
        assertEquals("Kebun Makmur", kebun.getNamaKebun());
        assertEquals(500, kebun.getLuasHektare());
        assertEquals("{}", kebun.getKoordinat());
        assertEquals(mandorId, kebun.getMandorId());
        assertEquals(mandorNama, kebun.getMandorNama());
        assertEquals(1, kebun.getSupirIds().size());
    }
}