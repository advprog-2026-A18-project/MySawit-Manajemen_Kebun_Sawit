package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.GenericResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.KebunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kebun")
public class KebunController {

    @Autowired
    private KebunService kebunService;

    @GetMapping
    public GenericResponse<List<KebunResponse>> getAllKebun(
            @RequestParam(required = false) String searchNama,
            @RequestParam(required = false) String searchKode) {

        List<KebunResponse> kebunList = kebunService.getAllKebun(searchNama, searchKode);
        return GenericResponse.success("Successfully retrieved kebun list", kebunList);
    }

    @GetMapping("/{kode}")
    public GenericResponse<KebunResponse> getKebunById(@PathVariable String kode) {
        KebunResponse kebun = kebunService.getKebunById(kode);
        if (kebun == null) {
            return GenericResponse.error(404, "Kebun not found");
        }
        return GenericResponse.success("Successfully retrieved kebun", kebun);
    }

    @PostMapping
    public GenericResponse<KebunResponse> createKebun(@RequestBody KebunRequest request) {
        KebunResponse created = kebunService.createKebun(request);
        return GenericResponse.success("Successfully created kebun", created);
    }
}
