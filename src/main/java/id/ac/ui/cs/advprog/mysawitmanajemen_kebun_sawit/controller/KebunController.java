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

    @PutMapping("/{kode}")
    public GenericResponse<KebunResponse> updateKebun(
            @PathVariable String kode,
            @RequestBody KebunRequest request) {
        KebunResponse updated = kebunService.updateKebun(kode, request);
        if (updated == null) {
            return GenericResponse.error(404, "Kebun not found");
        }
        return GenericResponse.success("Successfully updated kebun", updated);
    }

    @DeleteMapping("/{kode}")
    public GenericResponse<Void> deleteKebun(@PathVariable String kode) {
        try {
            kebunService.deleteKebun(kode);
            return GenericResponse.success("Successfully deleted kebun", null);
        } catch (IllegalStateException e) {
            return GenericResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{kode}/mandor")
    public GenericResponse<KebunResponse> assignMandor(
            @PathVariable String kode,
            @RequestParam String mandorId) {
        KebunResponse updated = kebunService.assignMandor(kode, mandorId);
        if (updated == null) {
            return GenericResponse.error(404, "Kebun not found");
        }
        return GenericResponse.success("Successfully assigned mandor", updated);
    }

    @DeleteMapping("/{kode}/mandor")
    public GenericResponse<KebunResponse> unassignMandor(
            @PathVariable String kode,
            @RequestParam(required = false) String targetKebunKode) {
        KebunResponse updated = kebunService.unassignMandor(kode, targetKebunKode);
        if (updated == null) {
            return GenericResponse.error(404, "Kebun not found");
        }
        return GenericResponse.success("Successfully unassigned mandor", updated);
    }

    @PostMapping("/{kode}/supir")
    public GenericResponse<KebunResponse> assignSupir(
            @PathVariable String kode,
            @RequestParam String supirId) {
        KebunResponse updated = kebunService.assignSupir(kode, supirId);
        if (updated == null) {
            return GenericResponse.error(404, "Kebun not found");
        }
        return GenericResponse.success("Successfully assigned supir", updated);
    }

    @DeleteMapping("/{kode}/supir/{supirId}")
    public GenericResponse<KebunResponse> unassignSupir(
            @PathVariable String kode,
            @PathVariable String supirId,
            @RequestParam(required = false) String targetKebunKode) {
        KebunResponse updated = kebunService.unassignSupir(kode, supirId, targetKebunKode);
        if (updated == null) {
            return GenericResponse.error(404, "Kebun not found");
        }
        return GenericResponse.success("Successfully unassigned supir", updated);
    }
}
