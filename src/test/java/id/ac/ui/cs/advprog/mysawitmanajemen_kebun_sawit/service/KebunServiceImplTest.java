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

        when(kebunRepository.findAllByOrderByKodeKebunAsc()).thenReturn(Arrays.asList(kebun1, kebun2));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun(null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(kebunRepository).findAllByOrderByKodeKebunAsc();
    }

    @Test
    void testGetAllKebun_SearchByNama() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");

        when(kebunRepository.findByNamaKebunContainingIgnoreCase("Makmur")).thenReturn(List.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun("Makmur", null, null);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllKebun_SearchByKode() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");
        kebun.setLuasHektare(500);
        kebun.setKoordinat("{}");

        when(kebunRepository.findByKodeKebunContainingIgnoreCase("KB")).thenReturn(List.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        List<KebunResponse> result = kebunService.getAllKebun(null, "KB", null);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllKebun_SortByCreatedAt() {
        when(kebunRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());
        kebunService.getAllKebun(null, null, "createdAt");
        verify(kebunRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetAllKebun_BlankSearch_UsesDefault() {
        when(kebunRepository.findAllByOrderByKodeKebunAsc()).thenReturn(List.of());
        kebunService.getAllKebun("   ", "   ", null);
        verify(kebunRepository).findAllByOrderByKodeKebunAsc();
    }

    @Test
    void testGetKebunById_Found() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setNamaKebun("Kebun Makmur");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.getKebunById("KB001", null);
        assertNotNull(result);
        assertEquals("KB001", result.getKodeKebun());
    }

    @Test
    void testGetKebunById_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        assertNull(kebunService.getKebunById("KB999", null));
    }

    @Test
    void testGetKebunById_WithSupirFilter() {
        KebunSupir supirA = new KebunSupir();
        supirA.setSupirId(UUID.randomUUID());
        supirA.setNamaSupir("Supir A");
        KebunSupir supirB = new KebunSupir();
        supirB.setSupirId(UUID.randomUUID());
        supirB.setNamaSupir("Supir B");

        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(supirA, supirB));

        KebunResponse result = kebunService.getKebunById("KB001", "Supir A");
        assertEquals(1, result.getSupirIds().size());
    }

    @Test
    void testCreateKebun_Success() {
        KebunRequest request = new KebunRequest("KB001", "Kebun Makmur", 500, "{}");
        Kebun savedKebun = new Kebun();
        savedKebun.setKodeKebun("KB001");
        savedKebun.setNamaKebun("Kebun Makmur");

        when(kebunRepository.findAll()).thenReturn(List.of());
        when(kebunRepository.save(any(Kebun.class))).thenReturn(savedKebun);
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.createKebun(request);
        assertNotNull(result);
        assertEquals("KB001", result.getKodeKebun());
    }

    @Test
    void testCreateKebun_Overlapping_Fails() {
        KebunRequest request = new KebunRequest("KB002", "Kebun Overlap", 500,
                "[{\"lat\":150,\"lng\":150},{\"lat\":250,\"lng\":150},{\"lat\":250,\"lng\":250},{\"lat\":150,\"lng\":250}]");
        Kebun existing = new Kebun();
        existing.setKodeKebun("KB001");
        existing.setNamaKebun("Kebun Nusantara");
        existing.setKoordinat("[{\"lat\":0,\"lng\":0},{\"lat\":400,\"lng\":0},{\"lat\":400,\"lng\":300},{\"lat\":0,\"lng\":300}]");

        when(kebunRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(IllegalStateException.class, () -> kebunService.createKebun(request));
    }

    @Test
    void testCreateKebun_NullKoordinat_Success() {
        KebunRequest request = new KebunRequest("KB001", "Kebun", 500, null);
        Kebun saved = new Kebun();
        saved.setKodeKebun("KB001");

        when(kebunRepository.save(any(Kebun.class))).thenReturn(saved);
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        assertNotNull(kebunService.createKebun(request));
    }

    @Test
    void testUpdateKebun_Success() {
        Kebun existing = new Kebun();
        existing.setKodeKebun("KB001");
        existing.setNamaKebun("Old");
        existing.setLuasHektare(500);
        existing.setKoordinat("{}");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(existing));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(existing);
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.updateKebun("KB001", new KebunRequest(null, "New", 750, null));
        assertEquals("New", result.getNamaKebun());
    }

    @Test
    void testUpdateKebun_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        assertNull(kebunService.updateKebun("KB999", new KebunRequest(null, "X", 1, null)));
    }

    @Test
    void testDeleteKebun_Success() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setMandorId(null);

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        assertDoesNotThrow(() -> kebunService.deleteKebun("KB001"));
        verify(kebunRepository).deleteById("KB001");
    }

    @Test
    void testDeleteKebun_FailsWithMandor() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setMandorId(UUID.randomUUID());

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        assertThrows(IllegalStateException.class, () -> kebunService.deleteKebun("KB001"));
    }

    // ===== Assign/Unassign Mandor =====

    @Test
    void testAssignMandor_Success() {
        UUID mandorId = UUID.randomUUID();
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(kebun);
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.assignMandor("KB001", mandorId, "Pak Mandor");
        assertNotNull(result);
        assertEquals(mandorId, result.getMandorId());
    }

    @Test
    void testAssignMandor_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        assertNull(kebunService.assignMandor("KB999", UUID.randomUUID(), "Pak Mandor"));
    }

    @Test
    void testUnassignMandor_NullTarget_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> kebunService.unassignMandor("KB001", null));
    }

    @Test
    void testUnassignMandor_BlankTarget_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> kebunService.unassignMandor("KB001", "   "));
    }

    @Test
    void testUnassignMandor_WithReassign_Success() {
        UUID mandorId = UUID.randomUUID();
        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setMandorId(mandorId);
        currentKebun.setMandorNama("Pak Mandor");

        Kebun targetKebun = new Kebun();
        targetKebun.setKodeKebun("KB002");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB002")).thenReturn(Optional.of(targetKebun));
        when(kebunRepository.save(any(Kebun.class))).thenAnswer(i -> i.getArgument(0));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.unassignMandor("KB001", "KB002");
        assertNotNull(result);
        assertNull(result.getMandorId());
        verify(kebunRepository, times(2)).save(any(Kebun.class));
    }

    @Test
    void testUnassignMandor_TargetNotFound_ThrowsException() {
        UUID mandorId = UUID.randomUUID();
        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");
        currentKebun.setMandorId(mandorId);
        currentKebun.setMandorNama("Pak Mandor");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> kebunService.unassignMandor("KB001", "KB999"));
    }

    @Test
    void testUnassignMandor_NoMandorAssigned_ReturnsCurrentState() {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");
        kebun.setMandorId(null);

        Kebun targetKebun = new Kebun();
        targetKebun.setKodeKebun("KB002");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunRepository.findById("KB002")).thenReturn(Optional.of(targetKebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());

        KebunResponse result = kebunService.unassignMandor("KB001", "KB002");
        assertNotNull(result);
        assertNull(result.getMandorId());
    }

    @Test
    void testUnassignMandor_CurrentKebunNotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        assertNull(kebunService.unassignMandor("KB999", "KB002"));
    }

    // ===== Assign/Unassign Supir =====

    @Test
    void testAssignSupir_Success() {
        UUID supirId = UUID.randomUUID();
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.save(any(KebunSupir.class))).thenReturn(new KebunSupir());

        KebunResponse result = kebunService.assignSupir("KB001", supirId, "Supir A");
        assertNotNull(result);
        verify(kebunSupirRepository).save(any(KebunSupir.class));
    }

    @Test
    void testAssignSupir_AlreadyAssigned() {
        UUID supirId = UUID.randomUUID();
        KebunSupir existing = new KebunSupir();
        existing.setSupirId(supirId);
        Kebun kebun = new Kebun();
        kebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(existing));

        kebunService.assignSupir("KB001", supirId, "Supir A");
        verify(kebunSupirRepository, never()).save(any());
    }

    @Test
    void testAssignSupir_NotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        assertNull(kebunService.assignSupir("KB999", UUID.randomUUID(), "Supir A"));
    }

    @Test
    void testUnassignSupir_NullTarget_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> kebunService.unassignSupir("KB001", UUID.randomUUID(), null));
    }

    @Test
    void testUnassignSupir_BlankTarget_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> kebunService.unassignSupir("KB001", UUID.randomUUID(), ""));
    }

    @Test
    void testUnassignSupir_WithReassign_Success() {
        UUID supirId = UUID.randomUUID();
        KebunSupir existingSupir = new KebunSupir();
        existingSupir.setKodeKebun("KB001");
        existingSupir.setSupirId(supirId);
        existingSupir.setNamaSupir("Supir A");

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(existingSupir));
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.save(any(KebunSupir.class))).thenReturn(new KebunSupir());

        KebunResponse result = kebunService.unassignSupir("KB001", supirId, "KB002");
        assertNotNull(result);
        verify(kebunSupirRepository).delete(existingSupir);
        verify(kebunSupirRepository).save(any(KebunSupir.class));
    }

    @Test
    void testUnassignSupir_TargetAlreadyHasSupir_NoSave() {
        UUID supirId = UUID.randomUUID();
        KebunSupir existingSupir = new KebunSupir();
        existingSupir.setKodeKebun("KB001");
        existingSupir.setSupirId(supirId);
        existingSupir.setNamaSupir("Supir A");

        KebunSupir targetSupir = new KebunSupir();
        targetSupir.setSupirId(supirId);

        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(existingSupir));
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(List.of(targetSupir));

        kebunService.unassignSupir("KB001", supirId, "KB002");
        verify(kebunSupirRepository).delete(existingSupir);
        verify(kebunSupirRepository, never()).save(any(KebunSupir.class));
    }

    @Test
    void testUnassignSupir_CurrentKebunNotFound() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());
        assertNull(kebunService.unassignSupir("KB999", UUID.randomUUID(), "KB002"));
    }

    @Test
    void testUnassignSupir_SupirNotInCurrentKebun_StillReassigns() {
        UUID supirId = UUID.randomUUID();
        Kebun currentKebun = new Kebun();
        currentKebun.setKodeKebun("KB001");

        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(currentKebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());
        when(kebunSupirRepository.save(any(KebunSupir.class))).thenReturn(new KebunSupir());

        KebunResponse result = kebunService.unassignSupir("KB001", supirId, "KB002");
        assertNotNull(result);
        verify(kebunSupirRepository, never()).delete(any(KebunSupir.class));
        verify(kebunSupirRepository).save(any(KebunSupir.class));
    }

    @Test
    void testCreateKebun_WithNullCoordInExisting() {
        KebunRequest request = new KebunRequest("KB002", "Kebun", 500, "[{\"lat\":100,\"lng\":100}]");
        Kebun existing = new Kebun();
        existing.setKodeKebun("KB001");
        existing.setKoordinat(null);

        Kebun saved = new Kebun();
        saved.setKodeKebun("KB002");
        when(kebunRepository.findAll()).thenReturn(List.of(existing));
        when(kebunRepository.save(any(Kebun.class))).thenReturn(saved);
        when(kebunSupirRepository.findByKodeKebun("KB002")).thenReturn(new ArrayList<>());

        assertNotNull(kebunService.createKebun(request));
    }
}
