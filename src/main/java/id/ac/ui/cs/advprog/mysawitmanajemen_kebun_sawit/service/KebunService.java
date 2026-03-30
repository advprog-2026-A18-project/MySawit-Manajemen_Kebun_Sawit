package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;

import java.util.List;

public interface KebunService {
    List<KebunResponse> getAllKebun(String searchNama, String searchKode);
    KebunResponse getKebunById(String kodeKebun);
    KebunResponse createKebun(KebunRequest request);
    KebunResponse updateKebun(String kodeKebun, KebunRequest request);
    void deleteKebun(String kodeKebun);
}