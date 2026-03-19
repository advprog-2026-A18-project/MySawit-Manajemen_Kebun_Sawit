package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

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
}
