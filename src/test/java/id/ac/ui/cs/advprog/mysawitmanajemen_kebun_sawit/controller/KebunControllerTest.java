package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.GenericResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.KebunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        KebunResponse kebun1 = new KebunResponse("KB001", "Kebun Makmur", 500, "{\"points\": [[0, 0], [200, 0], [200, 200], [0, 200]]}", null, null, Collections.emptyList());
        KebunResponse kebun2 = new KebunResponse("KB002", "Kebun Sejahtera", 750, "{\"points\": [[0, 0], [300, 0], [300, 250], [0, 250]]}", null, null, Collections.emptyList());

        List<KebunResponse> expectedList = Arrays.asList(kebun1, kebun2);
        when(kebunService.getAllKebun(null, null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun(null, null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(2, result.getData().size());
        assertEquals("KB001", result.getData().get(0).getKodeKebun());
        assertEquals("Kebun Makmur", result.getData().get(0).getNamaKebun());
        verify(kebunService, times(1)).getAllKebun(null, null);
    }

    @Test
    void testGetAllKebun_ReturnsEmptyList() {
        when(kebunService.getAllKebun(null, null)).thenReturn(List.of());

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun(null, null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(0, result.getData().size());
        verify(kebunService, times(1)).getAllKebun(null, null);
    }

    @Test
    void testGetAllKebun_WithSearch() {
        List<KebunResponse> expectedList = List.of(
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, Collections.emptyList())
        );
        when(kebunService.getAllKebun("Makmur", null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun("Makmur", null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getAllKebun("Makmur", null);
    }

    @Test
    void testGetAllKebun_WithSearchKode() {
        List<KebunResponse> expectedList = List.of(
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, Collections.emptyList())
        );
        when(kebunService.getAllKebun(null, "KB")).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun(null, "KB");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getAllKebun(null, "KB");
    }

    @Test
    void testGetAllKebun_WithBothFilters() {
        List<KebunResponse> expectedList = List.of(
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, Collections.emptyList())
        );
        when(kebunService.getAllKebun("Makmur", "KB")).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun("Makmur", "KB");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("Kebun Makmur", result.getData().get(0).getNamaKebun());
    }

    @Test
    void testGetKebunById_ReturnsKebun() {
        KebunResponse expected = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", "M001", null, Collections.emptyList());
        when(kebunService.getKebunById("KB001", null)).thenReturn(expected);

        GenericResponse<KebunResponse> result = kebunController.getKebunById("KB001", null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("KB001", result.getData().getKodeKebun());
        assertEquals("Kebun Makmur", result.getData().getNamaKebun());
        verify(kebunService, times(1)).getKebunById("KB001", null);
    }

    @Test
    void testGetKebunById_Returns404WhenNotFound() {
        when(kebunService.getKebunById("KB999", null)).thenReturn(null);

        GenericResponse<KebunResponse> result = kebunController.getKebunById("KB999", null);

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
        assertEquals("Kebun not found", result.getMessage());
        verify(kebunService, times(1)).getKebunById("KB999", null);
    }

    @Test
    void testGetKebunById_WithSupirFilter() {
        List<String> supirIds = List.of("SUP001", "SUP002");
        KebunResponse expected = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, supirIds);
        when(kebunService.getKebunById("KB001", "SUP")).thenReturn(expected);

        GenericResponse<KebunResponse> result = kebunController.getKebunById("KB001", "SUP");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getKebunById("KB001", "SUP");
    }

    // ===== Tests for Assign/Unassign Mandor =====

    @Test
    void testAssignMandor_ReturnsSuccess() {
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", "M001", null, Collections.emptyList());
        when(kebunService.assignMandor("KB001", "M001")).thenReturn(updated);

        GenericResponse<KebunResponse> result = kebunController.assignMandor("KB001", "M001");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("M001", result.getData().getMandorId());
    }

    @Test
    void testAssignMandor_Returns404WhenNotFound() {
        when(kebunService.assignMandor("KB999", "M001")).thenReturn(null);

        GenericResponse<KebunResponse> result = kebunController.assignMandor("KB999", "M001");

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
    }

    @Test
    void testUnassignMandor_ReturnsSuccess() {
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, Collections.emptyList());
        when(kebunService.unassignMandor("KB001", "KB002")).thenReturn(updated);

        GenericResponse<KebunResponse> result = kebunController.unassignMandor("KB001", "KB002");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    // ===== Tests for Assign/Unassign Supir =====

    @Test
    void testAssignSupir_ReturnsSuccess() {
        List<String> supirIds = List.of("SUP001");
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, supirIds);
        when(kebunService.assignSupir("KB001", "SUP001")).thenReturn(updated);

        GenericResponse<KebunResponse> result = kebunController.assignSupir("KB001", "SUP001");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void testUnassignSupir_ReturnsSuccess() {
        KebunResponse updated = new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null, Collections.emptyList());
        when(kebunService.unassignSupir("KB001", "SUP001", "KB002")).thenReturn(updated);

        GenericResponse<KebunResponse> result = kebunController.unassignSupir("KB001", "SUP001", "KB002");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void testUnassignSupir_Returns404WhenNotFound() {
        when(kebunService.unassignSupir("KB999", "SUP001", null)).thenReturn(null);

        GenericResponse<KebunResponse> result = kebunController.unassignSupir("KB999", "SUP001", null);

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
    }
}
