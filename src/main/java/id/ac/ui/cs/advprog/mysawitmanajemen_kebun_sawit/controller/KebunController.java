package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.AssignSupirRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.GenericResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.KebunService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/api/kebun")
public class KebunController {

    private final KebunService kebunService;

    public KebunController(KebunService kebunService) {
        this.kebunService = kebunService;
    }

    @GetMapping
    public GenericResponse<List<KebunResponse>> getAllKebun(
            @RequestParam(required = false) String searchNama,
            @RequestParam(required = false) String searchKode,
            @RequestParam(required = false) String sortBy) {

        List<KebunResponse> kebunList = kebunService.getAllKebun(searchNama, searchKode, sortBy);
        return GenericResponse.success("Successfully retrieved kebun list", kebunList);
    }

    @GetMapping("/{kode}")
    public GenericResponse<KebunResponse> getKebunById(
            @PathVariable String kode,
            @RequestParam(required = false) String searchSupirNama) {
        KebunResponse kebun = kebunService.getKebunById(kode, searchSupirNama);
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
        return GenericResponse.success("Successfully updated kebun", updated);
    }

    @DeleteMapping("/{kode}")
    public GenericResponse<Void> deleteKebun(@PathVariable String kode) {
        kebunService.deleteKebun(kode);
        return GenericResponse.success("Successfully deleted kebun", null);
    }

    @PostMapping("/{kode}/mandor")
    public GenericResponse<KebunResponse> assignMandor(
            @PathVariable String kode,
            @RequestBody AssignMandorRequest request) {
        KebunResponse updated = kebunService.assignMandor(kode, request.getMandorId(), request.getMandorName());
        return GenericResponse.success("Successfully assigned mandor", updated);
    }

    @DeleteMapping("/{kode}/mandor")
    public GenericResponse<KebunResponse> unassignMandor(
            @PathVariable String kode,
            @RequestParam String target) {
        KebunResponse updated = kebunService.unassignMandor(kode, target);
        return GenericResponse.success("Successfully unassigned mandor", updated);
    }

    @PostMapping("/{kode}/supir")
    public GenericResponse<KebunResponse> assignSupir(
            @PathVariable String kode,
            @RequestBody AssignSupirRequest request) {
        KebunResponse updated = kebunService.assignSupir(kode, request.getSupirId(), request.getNamaSupir());
        return GenericResponse.success("Successfully assigned supir", updated);
    }

    @DeleteMapping("/{kode}/supir/{supirId}")
    public GenericResponse<KebunResponse> unassignSupir(
            @PathVariable String kode,
            @PathVariable UUID supirId,
            @RequestParam String target) {
        KebunResponse updated = kebunService.unassignSupir(kode, supirId, target);
        return GenericResponse.success("Successfully unassigned supir", updated);
    }
}
