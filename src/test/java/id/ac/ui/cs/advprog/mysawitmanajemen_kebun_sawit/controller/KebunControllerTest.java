package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.AssignSupirRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.GenericResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.KebunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KebunControllerTest {

    @Mock
    private KebunService kebunService;

    @InjectMocks
    private KebunController kebunController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllKebun_ReturnsList() {
        KebunResponse kebun1 = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList());
        KebunResponse kebun2 = new KebunResponse("KB002", "Kebun Sejahtera", 750, "{}", null, null, null, Collections.emptyList(), Collections.emptyList());

        List<KebunResponse> expectedList = Arrays.asList(kebun1, kebun2);
        when(kebunService.getAllKebun(null, null, null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun(null, null, null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(2, result.getData().size());
        assertEquals("KB001", result.getData().get(0).getKodeKebun());
        assertEquals("Kebun Makmur", result.getData().get(0).getNamaKebun());
        verify(kebunService, times(1)).getAllKebun(null, null, null);
    }

    @Test
    void testGetAllKebun_ReturnsEmptyList() {
        when(kebunService.getAllKebun(null, null, null)).thenReturn(List.of());

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun(null, null, null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(0, result.getData().size());
        verify(kebunService, times(1)).getAllKebun(null, null, null);
    }

    @Test
    void testGetAllKebun_WithSearch() {
        List<KebunResponse> expectedList = List.of(
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList())
        );
        when(kebunService.getAllKebun("Makmur", null, null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun("Makmur", null, null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getAllKebun("Makmur", null, null);
    }

    @Test
    void testGetAllKebun_WithSearchKode() {
        List<KebunResponse> expectedList = List.of(
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList())
        );
        when(kebunService.getAllKebun(null, "KB", null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun(null, "KB", null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getAllKebun(null, "KB", null);
    }

    @Test
    void testGetAllKebun_WithBothFilters() {
        List<KebunResponse> expectedList = List.of(
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList())
        );
        when(kebunService.getAllKebun("Makmur", "KB", null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun("Makmur", "KB", null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("Kebun Makmur", result.getData().get(0).getNamaKebun());
    }

    @Test
    void testGetKebunById_ReturnsKebun() {
        UUID mandorId = UUID.randomUUID();
        KebunResponse expected = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", mandorId, "Pak Mandor", null, Collections.emptyList(), Collections.emptyList());
        when(kebunService.getKebunById("KB001", null)).thenReturn(expected);

        GenericResponse<KebunResponse> result = kebunController.getKebunById("KB001", null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("KB001", result.getData().getKodeKebun());
        assertEquals("Kebun Makmur", result.getData().getNamaKebun());
        verify(kebunService, times(1)).getKebunById("KB001", null);
    }

    @Test
    void testGetKebunById_NotFound_ThrowsException() {
        when(kebunService.getKebunById("KB999", null))
                .thenThrow(new KebunNotFoundException("KB999"));

        assertThrows(KebunNotFoundException.class,
                () -> kebunController.getKebunById("KB999", null));
    }

    @Test
    void testGetKebunById_WithSupirFilter() {
        List<UUID> supirIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<String> supirNamas = List.of("Supir A", "Supir B");
        KebunResponse expected = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, supirIds, supirNamas);
        when(kebunService.getKebunById("KB001", "SUP")).thenReturn(expected);

        GenericResponse<KebunResponse> result = kebunController.getKebunById("KB001", "SUP");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getKebunById("KB001", "SUP");
    }

    // ===== Tests for Assign/Unassign Mandor =====

    @Test
    void testAssignMandor_ReturnsSuccess() {
        UUID mandorId = UUID.randomUUID();
        String namaMandor = "Pak Mandor";
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", mandorId, namaMandor, null, Collections.emptyList(), Collections.emptyList());
        when(kebunService.assignMandor("KB001", mandorId, namaMandor)).thenReturn(updated);

        AssignMandorRequest request = new AssignMandorRequest(mandorId, namaMandor, null);
        GenericResponse<KebunResponse> result = kebunController.assignMandor("KB001", request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(mandorId, result.getData().getMandorId());
    }

    @Test
    void testAssignMandor_NotFound_ThrowsException() {
        UUID mandorId = UUID.randomUUID();
        when(kebunService.assignMandor("KB999", mandorId, "Pak Mandor"))
                .thenThrow(new KebunNotFoundException("KB999"));

        AssignMandorRequest request = new AssignMandorRequest(mandorId, "Pak Mandor", null);
        assertThrows(KebunNotFoundException.class,
                () -> kebunController.assignMandor("KB999", request));
    }

    @Test
    void testUnassignMandor_ReturnsSuccess() {
        UUID targetKebunKode = UUID.randomUUID();
        String target = targetKebunKode.toString();
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList());
        when(kebunService.unassignMandor("KB001", target)).thenReturn(updated);

        GenericResponse<KebunResponse> result = kebunController.unassignMandor("KB001", target);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void testUnassignMandor_NotFound_ThrowsException() {
        when(kebunService.unassignMandor("KB999", "KB002"))
                .thenThrow(new KebunNotFoundException("KB999"));

        assertThrows(KebunNotFoundException.class,
                () -> kebunController.unassignMandor("KB999", "KB002"));
    }

    // ===== Tests for Assign/Unassign Supir =====

    @Test
    void testAssignSupir_ReturnsSuccess() {
        UUID supirId = UUID.randomUUID();
        String supirNama = "Supir A";
        List<UUID> supirIds = List.of(supirId);
        List<String> supirNamas = List.of(supirNama);
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, supirIds, supirNamas);
        when(kebunService.assignSupir("KB001", supirId, supirNama)).thenReturn(updated);

        AssignSupirRequest request = new AssignSupirRequest(supirId, supirNama, null);
        GenericResponse<KebunResponse> result = kebunController.assignSupir("KB001", request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void testUnassignSupir_ReturnsSuccess() {
        UUID supirId = UUID.randomUUID();
        UUID targetKebunKode = UUID.randomUUID();
        String target = targetKebunKode.toString();
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList());
        when(kebunService.unassignSupir("KB001", supirId, target)).thenReturn(updated);

        GenericResponse<KebunResponse> result = kebunController.unassignSupir("KB001", supirId, target);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void testUnassignSupir_NotFound_ThrowsException() {
        UUID supirId = UUID.randomUUID();
        when(kebunService.unassignSupir("KB999", supirId, "KB002"))
                .thenThrow(new KebunNotFoundException("KB999"));

        assertThrows(KebunNotFoundException.class,
                () -> kebunController.unassignSupir("KB999", supirId, "KB002"));
    }

    @Test
    void testUpdateKebun_NotFound_ThrowsException() {
        KebunRequest request = new KebunRequest(null, "Kebun Baru", 750, null);
        when(kebunService.updateKebun("KB999", request))
                .thenThrow(new KebunNotFoundException("KB999"));

        assertThrows(KebunNotFoundException.class,
                () -> kebunController.updateKebun("KB999", request));
    }

    @Test
    void testAssignSupir_NotFound_ThrowsException() {
        UUID supirId = UUID.randomUUID();
        when(kebunService.assignSupir("KB999", supirId, "Supir A"))
                .thenThrow(new KebunNotFoundException("KB999"));

        AssignSupirRequest request = new AssignSupirRequest(supirId, "Supir A", null);
        assertThrows(KebunNotFoundException.class,
                () -> kebunController.assignSupir("KB999", request));
    }

    @Test
    void testDeleteKebun_WithMandor_ThrowsException() {
        doThrow(new IllegalStateException("Cannot delete kebun with assigned mandor"))
                .when(kebunService).deleteKebun("KB001");

        assertThrows(IllegalStateException.class,
                () -> kebunController.deleteKebun("KB001"));
    }

    @Test
    void testDeleteKebun_ReturnsSuccess() {
        doNothing().when(kebunService).deleteKebun("KB001");

        GenericResponse<Void> result = kebunController.deleteKebun("KB001");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void testCreateKebun_ReturnsSuccess() {
        KebunRequest request = new KebunRequest("KB001", "Kebun Baru", 500, "{}");
        KebunResponse expected = new KebunResponse("KB001", "Kebun Baru", 500, "{}", null, null, null, Collections.emptyList(), Collections.emptyList());
        when(kebunService.createKebun(request)).thenReturn(expected);

        GenericResponse<KebunResponse> result = kebunController.createKebun(request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("KB001", result.getData().getKodeKebun());
    }
}