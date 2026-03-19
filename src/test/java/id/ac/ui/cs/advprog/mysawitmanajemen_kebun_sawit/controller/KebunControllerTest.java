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
        KebunResponse kebun1 = new KebunResponse("KB001", "Kebun Makmur", 500, "{\"points\": [[0, 0], [200, 0], [200, 200], [0, 200]]}", null, null);
        KebunResponse kebun2 = new KebunResponse("KB002", "Kebun Sejahtera", 750, "{\"points\": [[0, 0], [300, 0], [300, 250], [0, 250]]}", null, null);

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
            new KebunResponse("KB001", "Kebun Makmur", 500, "{}", null, null)
        );
        when(kebunService.getAllKebun("Makmur", null)).thenReturn(expectedList);

        GenericResponse<List<KebunResponse>> result = kebunController.getAllKebun("Makmur", null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        verify(kebunService, times(1)).getAllKebun("Makmur", null);
    }
}
