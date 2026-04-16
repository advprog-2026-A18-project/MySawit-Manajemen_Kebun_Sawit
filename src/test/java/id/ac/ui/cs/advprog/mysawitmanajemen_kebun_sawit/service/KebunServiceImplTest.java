package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KebunServiceImplTest {

    @Mock
    private KebunRepository kebunRepository;

    @InjectMocks
    private KebunServiceImpl kebunService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllKebun_NoFilter_ReturnsAll() {
        Kebun kebun1 = new Kebun();
        kebun1.setKodeKebun("KB001");
        kebun1.setNamaKebun("Kebun Makmur");
        kebun1.setLuasHektare(500);
        kebun1.setKoordinat("{}");

        Kebun kebun2 = new Kebun();
        kebun2.setKodeKebun("KB002");
        kebun2.setNamaKebun("Kebun Sejahtera");
        kebun2.setLuasHektare(750);
        kebun2.setKoordinat("{}");

        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(kebun1, kebun2));

        List<KebunResponse> result = kebunService.getAllKebun(null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("KB001", result.get(0).getKodeKebun());
        verify(kebunRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetAllKebun_SearchByNama_FiltersByNama() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");

        when(kebunRepository.findByNamaKebunContainingIgnoreCase("Makmur")).thenReturn(Arrays.asList(kebun));

        List<KebunResponse> result = kebunService.getAllKebun("Makmur", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Kebun Makmur", result.get(0).getNamaKebun());
        verify(kebunRepository, times(1)).findByNamaKebunContainingIgnoreCase("Makmur");
    }

    @Test
    void testGetAllKebun_SearchByKode_FiltersByKode() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");

        when(kebunRepository.findByKodeKebunContainingIgnoreCase("KB")).thenReturn(Arrays.asList(kebun));

        List<KebunResponse> result = kebunService.getAllKebun(null, "KB");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("KB001", result.get(0).getKodeKebun());
        verify(kebunRepository, times(1)).findByKodeKebunContainingIgnoreCase("KB");
    }

    @Test
    void testGetAllKebun_EmptyResult_ReturnsEmptyList() {
        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        List<KebunResponse> result = kebunService.getAllKebun(null, null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllKebun_NamaHasPriorityOverKode() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");

        when(kebunRepository.findByNamaKebunContainingIgnoreCase("Makmur")).thenReturn(Arrays.asList(kebun));

        List<KebunResponse> result = kebunService.getAllKebun("Makmur", "KB001");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(kebunRepository, times(1)).findByNamaKebunContainingIgnoreCase("Makmur");
        verify(kebunRepository, never()).findByKodeKebunContainingIgnoreCase(anyString());
    }

    @Test
    void testGetKebunById_Found() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");
        kebun.setMandorId("M001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));

        KebunResponse result = kebunService.getKebunById("KB001", null);

        assertNotNull(result);
        assertEquals("KB001", result.getKodeKebun());
        assertEquals("Kebun Makmur", result.getNamaKebun());
        assertEquals(500, result.getLuasHektare());
        assertEquals("M001", result.getMandorId());
    }

    @Test
    void testGetKebunById_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.getKebunById("KB999", null);

        assertNull(result);
    }

    @Test
    void testGetAllKebun_WithBlankSearch_UsesDefault() {
        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        List<KebunResponse> result = kebunService.getAllKebun("   ", "   ");

        assertNotNull(result);
        verify(kebunRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetAllKebun_WithEmptyString_UsesDefault() {
        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        List<KebunResponse> result = kebunService.getAllKebun("", "");

        assertNotNull(result);
        verify(kebunRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    // ===== Tests for CRUD =====

    @Test
    void testCreateKebun_Success() {
        KebunRequest request = new KebunRequest("KB001", "Kebun Makmur", 500, "{}");

        Kebun savedKebun = new Kebun();
        savedKebun.setKodeKebun("KB001");
        savedKebun.setNamaKebun("Kebun Makmur");
        savedKebun.setLuasHektare(500);
        savedKebun.setKoordinat("{}");

        when(kebunRepository.findAll()).thenReturn(List.of());
        when(kebunRepository.save(any(Kebun.class))).thenReturn(savedKebun);

        KebunResponse result = kebunService.createKebun(request);

        assertNotNull(result);
        assertEquals("KB001", result.getKodeKebun());
        assertEquals("Kebun Makmur", result.getNamaKebun());
        verify(kebunRepository, times(1)).save(any(Kebun.class));
    }

    @Test
    void testCreateKebun_WithOverlapping_Fails() {
        KebunRequest request = new KebunRequest("KB002", "Kebun Overlap", 500, "[{\"lat\":150,\"lng\":150},{\"lat\":250,\"lng\":150},{\"lat\":250,\"lng\":250},{\"lat\":150,\"lng\":250}]");

        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setNamaKebun("Kebun Nusantara");
        existingKebun.setKoordinat("[{\"lat\":0,\"lng\":0},{\"lat\":400,\"lng\":0},{\"lat\":400,\"lng\":300},{\"lat\":0,\"lng\":300}]");

        when(kebunRepository.findAll()).thenReturn(List.of(existingKebun));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            kebunService.createKebun(request);
        });

        assertTrue(exception.getMessage().contains("cannot overlap"));
    }

    @Test
    void testCreateKebun_NullKoordinat_Success() {
        KebunRequest request = new KebunRequest("KB001", "Kebun Makmur", 500, null);

        Kebun savedKebun = new Kebun();
        savedKebun.setKodeKebun("KB001");
        savedKebun.setNamaKebun("Kebun Makmur");

        when(kebunRepository.save(any(Kebun.class))).thenReturn(savedKebun);

        KebunResponse result = kebunService.createKebun(request);

        assertNotNull(result);
    }

    @Test
    void testUpdateKebun_Success() {
        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setNamaKebun("Kebun Lama");
        existingKebun.setLuasHektare(500);
        existingKebun.setKoordinat("{}");

        KebunRequest request = new KebunRequest(null, "Kebun Baru", 750, null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(existingKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(existingKebun);

        KebunResponse result = kebunService.updateKebun("KB001", request);

        assertNotNull(result);
        assertEquals("Kebun Baru", result.getNamaKebun());
        assertEquals(750, result.getLuasHektare());
    }

    @Test
    void testUpdateKebun_NotFound() {
        KebunRequest request = new KebunRequest(null, "Kebun Baru", 750, null);

        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.updateKebun("KB999", request);

        assertNull(result);
    }

    @Test
    void testUpdateKebun_PartialUpdate() {
        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setNamaKebun("Kebun Makmur");
        existingKebun.setLuasHektare(500);
        existingKebun.setKoordinat("{}");

        KebunRequest request = new KebunRequest(null, null, 750, null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(existingKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(existingKebun);

        KebunResponse result = kebunService.updateKebun("KB001", request);

        assertNotNull(result);
        assertEquals(750, result.getLuasHektare());
        assertEquals("Kebun Makmur", result.getNamaKebun());
    }

    @Test
    void testDeleteKebun_Success() {
        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(existingKebun));

        assertDoesNotThrow(() -> kebunService.deleteKebun("KB001"));
        verify(kebunRepository, times(1)).deleteById("KB001");
    }

    @Test
    void testDeleteKebun_FailsWithMandor() {
        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setMandorId("M001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(existingKebun));

        assertThrows(IllegalStateException.class, () -> {
            kebunService.deleteKebun("KB001");
        });
    }

    @Test
    void testDeleteKebun_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> kebunService.deleteKebun("KB999"));
        verify(kebunRepository, never()).deleteById(anyString());
    }

    // ===== Tests for Assign/Unassign Mandor =====

    @Test
    void testAssignMandor_Success() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(kebun);

        KebunResponse result = kebunService.assignMandor("KB001", "M001");

        assertNotNull(result);
        assertEquals("M001", result.getMandorId());
        verify(kebunRepository, times(1)).save(kebun);
    }

    @Test
    void testAssignMandor_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.assignMandor("KB999", "M001");

        assertNull(result);
    }

    @Test
    void testUnassignMandor_WithReassign() {
        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setMandorId("M001");

        Kebun targetKebun = new Kebun();
        targetKebun.setKodeKebun("KB002");
        targetKebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB002")).thenReturn(Optional.of(targetKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);

        KebunResponse result = kebunService.unassignMandor("KB001", "KB002");

        assertNotNull(result);
        assertNull(result.getMandorId());
    }

    @Test
    void testUnassignMandor_NoMandorToUnassign() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));

        KebunResponse result = kebunService.unassignMandor("KB001", null);

        assertNotNull(result);
        assertNull(result.getMandorId());
    }

    // ===== Tests for Assign/Unassign Supir =====

    @Test
    void testAssignSupir_Success() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setSupirIds(new ArrayList<>());

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(kebun);

        KebunResponse result = kebunService.assignSupir("KB001", "SUP001");

        assertNotNull(result);
        assertEquals(1, result.getSupirIds().size());
        assertTrue(result.getSupirIds().contains("SUP001"));
    }

    @Test
    void testAssignSupir_AlreadyAssigned() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");

        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setSupirIds(supirIds);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));

        KebunResponse result = kebunService.assignSupir("KB001", "SUP001");

        assertNotNull(result);
        assertEquals(1, result.getSupirIds().size());
        verify(kebunRepository, never()).save(any());
    }

    @Test
    void testUnassignSupir_WithReassign() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setSupirIds(supirIds);

        Kebun targetKebun = new Kebun();
        targetKebun.setKodeKebun("KB002");
        targetKebun.setSupirIds(new ArrayList<>());

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB002")).thenReturn(Optional.of(targetKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);

        KebunResponse result = kebunService.unassignSupir("KB001", "SUP001", "KB002");

        assertNotNull(result);
        assertFalse(result.getSupirIds().contains("SUP001"));
    }

    @Test
    void testUnassignSupir_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.unassignSupir("KB999", "SUP001", null);

        assertNull(result);
    }

    // ===== Additional tests for coverage =====

    @Test
    void testGetKebunById_WithSupirFilter() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");
        supirIds.add("SUP002");

        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setSupirIds(supirIds);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));

        KebunResponse result = kebunService.getKebunById("KB001", "SUP");

        assertNotNull(result);
        assertEquals(2, result.getSupirIds().size());
    }

    @Test
    void testGetKebunById_WithSupirFilter_NoMatch() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");

        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setSupirIds(supirIds);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));

        KebunResponse result = kebunService.getKebunById("KB001", "XYZ");

        assertNotNull(result);
        assertEquals(0, result.getSupirIds().size());
    }

    @Test
    void testCreateKebun_WithNullKoordinat() {
        KebunRequest request = new KebunRequest("KB001", "Kebun Makmur", 500, null);

        Kebun savedKebun = new Kebun();
        savedKebun.setKodeKebun("KB001");
        savedKebun.setNamaKebun("Kebun Makmur");

        when(kebunRepository.save(any(Kebun.class))).thenReturn(savedKebun);

        KebunResponse result = kebunService.createKebun(request);

        assertNotNull(result);
    }

    @Test
    void testUpdateKebun_WithKoordinat() {
        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setNamaKebun("Kebun Lama");
        existingKebun.setLuasHektare(500);
        existingKebun.setKoordinat("{}");

        KebunRequest request = new KebunRequest(null, null, null, "[{\"lat\":0,\"lng\":0},{\"lat\":100,\"lng\":0},{\"lat\":100,\"lng\":100},{\"lat\":0,\"lng\":100}]");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(existingKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(existingKebun);

        KebunResponse result = kebunService.updateKebun("KB001", request);

        assertNotNull(result);
    }

    @Test
    void testUnassignMandor_TargetKebunNotFound() {
        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setMandorId("M001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);

        KebunResponse result = kebunService.unassignMandor("KB001", "KB999");

        assertNotNull(result);
        assertNull(result.getMandorId());
    }

    @Test
    void testUnassignMandor_BlankTargetKode() {
        Kebun currentKebun = newKebun("KB001", "M001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);

        KebunResponse result = kebunService.unassignMandor("KB001", "   ");

        assertNotNull(result);
        assertNull(result.getMandorId());
    }

    @Test
    void testAssignSupir_NullSupirIdsList() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setSupirIds(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(kebun);

        KebunResponse result = kebunService.assignSupir("KB001", "SUP001");

        assertNotNull(result);
    }

    @Test
    void testUnassignSupir_NullSupirIds() {
        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setSupirIds(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));

        KebunResponse result = kebunService.unassignSupir("KB001", "SUP001", null);

        assertNotNull(result);
    }

    @Test
    void testUnassignSupir_TargetNotFound() {
        List<String> supirIds = new ArrayList<>();
        supirIds.add("SUP001");

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setSupirIds(supirIds);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);

        KebunResponse result = kebunService.unassignSupir("KB001", "SUP001", "KB999");

        assertNotNull(result);
        assertFalse(result.getSupirIds().contains("SUP001"));
    }

    @Test
    void testUnassignSupir_ReassignToTargetWithExistingSupir() {
        List<String> currentSupirIds = new ArrayList<>();
        currentSupirIds.add("SUP001");

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setSupirIds(currentSupirIds);

        List<String> targetSupirIds = new ArrayList<>();
        targetSupirIds.add("SUP002");

        Kebun targetKebun = new Kebun();
        targetKebun.setKodeKebun("KB002");
        targetKebun.setSupirIds(targetSupirIds);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB002")).thenReturn(Optional.of(targetKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);

        KebunResponse result = kebunService.unassignSupir("KB001", "SUP001", "KB002");

        assertNotNull(result);
        assertFalse(result.getSupirIds().contains("SUP001"));
    }

    private Kebun newKebun(String kode, String mandorId) {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun(kode);
        kebun.setMandorId(mandorId);
        return kebun;
    }
}
