package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;
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

class KebunControllerTest {

    @Mock
    private KebunRepository kebunRepository;

    @InjectMocks
    private KebunController kebunController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllKebun_shouldReturnListOfKebun() {
        Kebun kebun1 = new Kebun();
        kebun1.setKodeKebun("KB001");
        kebun1.setNamaKebun("Kebun Makmur");
        kebun1.setLuasHektare(500);
        kebun1.setKoordinat("{\"points\": [[0, 0], [200, 0], [200, 200], [0, 200]]}");

        Kebun kebun2 = new Kebun();
        kebun2.setKodeKebun("KB002");
        kebun2.setNamaKebun("Kebun Sejahtera");
        kebun2.setLuasHektare(750);
        kebun2.setKoordinat("{\"points\": [[0, 0], [300, 0], [300, 250], [0, 250]]}");

        List<Kebun> expectedList = Arrays.asList(kebun1, kebun2);
        when(kebunRepository.findAll()).thenReturn(expectedList);

        List<Kebun> result = kebunController.getAllKebun();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("KB001", result.get(0).getKodeKebun());
        assertEquals("Kebun Makmur", result.get(0).getNamaKebun());
        assertEquals("KB002", result.get(1).getKodeKebun());
        assertEquals("Kebun Sejahtera", result.get(1).getNamaKebun());
        verify(kebunRepository, times(1)).findAll();
    }

    @Test
    void getAllKebun_shouldReturnEmptyListWhenNoData() {
        when(kebunRepository.findAll()).thenReturn(List.of());

        List<Kebun> result = kebunController.getAllKebun();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(kebunRepository, times(1)).findAll();
    }
}
