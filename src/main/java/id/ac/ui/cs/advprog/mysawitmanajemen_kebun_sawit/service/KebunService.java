package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;

import java.util.List;
import java.util.UUID;

public interface KebunService {
    List<KebunResponse> getAllKebun(String searchNama, String searchKode, String sortBy);
    KebunResponse getKebunById(String kodeKebun, String searchSupirNama);
    KebunResponse createKebun(KebunRequest request);
    KebunResponse updateKebun(String kodeKebun, KebunRequest request);
    void deleteKebun(String kodeKebun);
    KebunResponse assignMandor(String kodeKebun, UUID mandorId, String mandorNama);
    KebunResponse unassignMandor(String kodeKebun, String targetKebunKode);
    KebunResponse assignSupir(String kodeKebun, UUID supirId, String namaSupir);
    KebunResponse unassignSupir(String kodeKebun, UUID supirId, String targetKebunKode);
}