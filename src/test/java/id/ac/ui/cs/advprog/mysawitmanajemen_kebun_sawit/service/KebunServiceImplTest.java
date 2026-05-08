package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunSupirRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KebunServiceImplTest {

    @Mock
    private KebunRepository kebunRepository;

    @Mock
    private KebunSupirRepository kebunSupirRepository;

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
        when(kebunRepository.findAllByOrderByKodeKebunAsc()).thenReturn(Arrays.asList(kebun1, kebun2));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun(null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("KB001", result.get(0).getKodeKebun());
        verify(kebunRepository, times(1)).findAllByOrderByKodeKebunAsc();
    }

    @Test
    void testGetAllKebun_SearchByNama_FiltersByNama() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");

        when(kebunRepository.findByNamaKebunContainingIgnoreCase("Makmur")).thenReturn(Arrays.asList(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun("Makmur", null, null);

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
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun(null, "KB", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("KB001", result.get(0).getKodeKebun());
        verify(kebunRepository, times(1)).findByKodeKebunContainingIgnoreCase("KB");
    }

    @Test
    void testGetAllKebun_EmptyResult_ReturnsEmptyList() {
        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        List<KebunResponse> result = kebunService.getAllKebun(null, null, null);

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

        List<KebunResponse> result = kebunService.getAllKebun("Makmur", "KB001", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(kebunRepository, times(1)).findByNamaKebunContainingIgnoreCase("Makmur");
        verify(kebunRepository, never()).findByKodeKebunContainingIgnoreCase(anyString());
    }

    @Test
    void testGetKebunById_Found() {
        UUID mandorId = UUID.randomUUID();
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");
        kebun.setMandorId(mandorId);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.getKebunById("KB001", null);

        assertNotNull(result);
        assertEquals("KB001", result.getKodeKebun());
        assertEquals("Kebun Makmur", result.getNamaKebun());
        assertEquals(500, result.getLuasHektare());
        assertEquals(mandorId, result.getMandorId());
    }

    @Test
    void testGetKebunById_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.getKebunById("KB999", null);

        assertNull(result);
    }

    @Test
    void testGetAllKebun_WithBlankSearch_UsesDefault() {
        when(kebunRepository.findAllByOrderByKodeKebunAsc()).thenReturn(Arrays.asList());

        List<KebunResponse> result = kebunService.getAllKebun("   ", "   ", null);

        assertNotNull(result);
        verify(kebunRepository, times(1)).findAllByOrderByKodeKebunAsc();
    }

    @Test
    void testGetAllKebun_WithEmptyString_UsesDefault() {
        when(kebunRepository.findAllByOrderByKodeKebunAsc()).thenReturn(Arrays.asList());

        List<KebunResponse> result = kebunService.getAllKebun("", "", null);

        assertNotNull(result);
        verify(kebunRepository, times(1)).findAllByOrderByKodeKebunAsc();
    }

    @Test
    void testGetAllKebun_DefaultSortByKodeKebun() {
        Kebun sort1 = new Kebun();
        sort1.setKodeKebun("KB001");
        sort1.setNamaKebun("Kebun Makmur");
        sort1.setLuasHektare(500);
        sort1.setKoordinat("{}");

        Kebun sort2 = new Kebun();
        sort2.setKodeKebun("KB002");
        sort2.setNamaKebun("Kebun Sejahtera");
        sort2.setLuasHektare(750);
        sort2.setKoordinat("{}");

        when(kebunRepository.findAllByOrderByKodeKebunAsc()).thenReturn(Arrays.asList(sort1, sort2));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun(null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(kebunRepository, times(1)).findAllByOrderByKodeKebunAsc();
    }

    @Test
    void testGetAllKebun_SortByCreatedAt() {
        Kebun sort1 = new Kebun();
        sort1.setKodeKebun("KB001");
        sort1.setNamaKebun("Kebun Makmur");
        sort1.setLuasHektare(500);
        sort1.setKoordinat("{}");

        Kebun sort2 = new Kebun();
        sort2.setKodeKebun("KB002");
        sort2.setNamaKebun("Kebun Sejahtera");
        sort2.setLuasHektare(750);
        sort2.setKoordinat("{}");

        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(sort1, sort2));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun(null, null, "createdAt");

        assertNotNull(result);
        assertEquals(2, result.size());
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
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

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
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

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
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

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
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

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
        verify(kebunSupirRepository, times(1)).deleteByKodeKebun("KB001");
        verify(kebunRepository, times(1)).deleteById("KB001");
    }

    @Test
    void testDeleteKebun_FailsWithMandor() {
        UUID mandorId = UUID.randomUUID();
        Kebun existingKebun = new Kebun();
        existingKebun.setKodeKebun("KB001");
        existingKebun.setMandorId(mandorId);

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
        UUID mandorId = UUID.randomUUID();
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(kebun);
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.assignMandor("KB001", mandorId, "Pak Mandor");

        assertNotNull(result);
        assertEquals(mandorId, result.getMandorId());
        verify(kebunRepository, times(1)).save(kebun);
    }

    @Test
    void testAssignMandor_NotFound() {
        UUID mandorId = UUID.randomUUID();
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.assignMandor("KB999", mandorId, "Pak Mandor");

        assertNull(result);
    }

    @Test
    void testUnassignMandor_NoMandorToUnassign() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.unassignMandor("KB001", null);

        assertNotNull(result);
        assertNull(result.getMandorId());
    }

    // ===== Tests for Assign/Unassign Supir =====

    @Test
    void testAssignSupir_Success() {
        UUID supirId = UUID.randomUUID();
        String namaSupir = "Supir A";

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(new Kebun()));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.save(any(KebunSupir.class))).thenReturn(new KebunSupir());

        KebunResponse result = kebunService.assignSupir("KB001", supirId, namaSupir);

        assertNotNull(result);
        verify(kebunSupirRepository, times(1)).save(any(KebunSupir.class));
    }

    @Test
    void testAssignSupir_AlreadyAssigned() {
        UUID supirId = UUID.randomUUID();
        KebunSupir existingSupir = new KebunSupir();
        existingSupir.setSupirId(supirId);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(new Kebun()));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(existingSupir));

        KebunResponse result = kebunService.assignSupir("KB001", supirId, "Supir A");

        assertNotNull(result);
        verify(kebunSupirRepository, never()).save(any());
    }

    @Test
    void testUnassignSupir_NotFound() {
        UUID supirId = UUID.randomUUID();
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        KebunResponse result = kebunService.unassignSupir("KB999", supirId, null);

        assertNull(result);
    }

    // ===== Additional tests for coverage =====

    @Test
    void testGetKebunById_WithSupir() {
        UUID supirId = UUID.randomUUID();
        String namaSupir = "Supir A";
        KebunSupir supir = new KebunSupir();
        supir.setSupirId(supirId);
        supir.setNamaSupir(namaSupir);

        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        List<KebunSupir> supirList = new ArrayList<>();
        supirList.add(supir);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(supirList);

        KebunResponse result = kebunService.getKebunById("KB001", null);

        assertNotNull(result);
        assertEquals(1, result.getSupirIds().size());
        assertEquals(supirId, result.getSupirIds().get(0));
        assertEquals(namaSupir, result.getListSupir().get(0));
    }

    @Test
    void testUnassignSupir_WithReassign() {
        UUID supirId = UUID.randomUUID();
        String targetKebunKode = "KB002";
        String namaSupir = "Supir A";

        KebunSupir existingSupir = new KebunSupir();
        existingSupir.setKodeKebun("KB001");
        existingSupir.setSupirId(supirId);
        existingSupir.setNamaSupir(namaSupir);

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(existingSupir));
        when(kebunSupirRepository.findByKodeKebun(targetKebunKode)).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.save(any(KebunSupir.class))).thenReturn(new KebunSupir());

        KebunResponse result = kebunService.unassignSupir("KB001", supirId, targetKebunKode);

        assertNotNull(result);
        verify(kebunSupirRepository, times(1)).delete(any(KebunSupir.class));
        verify(kebunSupirRepository, times(1)).save(any(KebunSupir.class));
    }

    @Test
    void testUnassignMandor_WithReassign() {
        UUID mandorId = UUID.randomUUID();
        String targetKebunKode = "KB002";

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setMandorId(mandorId);
        currentKebun.setMandorNama("Pak Mandor");

        Kebun targetKebun = new Kebun();
        targetKebun.setKodeKebun(targetKebunKode);
        targetKebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById(targetKebunKode)).thenReturn(Optional.of(targetKebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(currentKebun);
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.findByKodeKebun(targetKebunKode)).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.unassignMandor("KB001", targetKebunKode);

        assertNotNull(result);
        assertNull(result.getMandorId());
    }
}