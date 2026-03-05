package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api") // Menggunakan prefix /api
public class KebunController {

    @Autowired
    private KebunRepository kebunRepository;

    @GetMapping("/kebun") // Gabungan menjadi /api/kebun
    public List<Kebun> getAllKebun() {
        return kebunRepository.findAll();
    }
}
