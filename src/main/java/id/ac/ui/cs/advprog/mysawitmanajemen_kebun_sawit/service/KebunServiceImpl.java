package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KebunServiceImpl implements KebunService {

    @Autowired
    private KebunRepository kebunRepository;

    @Override
    public List<KebunResponse> getAllKebun(String searchNama, String searchKode) {
        List<Kebun> kebunList;

        if (searchNama != null && !searchNama.isBlank()) {
            kebunList = kebunRepository.findByNamaKebunContainingIgnoreCase(searchNama);
        } else if (searchKode != null && !searchKode.isBlank()) {
            kebunList = kebunRepository.findByKodeKebunContainingIgnoreCase(searchKode);
        } else {
            kebunList = kebunRepository.findAllByOrderByCreatedAtDesc();
        }

        return kebunList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KebunResponse getKebunById(String kodeKebun) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }
        return toResponse(optionalKebun.get());
    }

    @Override
    public KebunResponse createKebun(KebunRequest request) {
        Kebun kebun = new Kebun();
        kebun.setKodeKebun(request.getKodeKebun());
        kebun.setNamaKebun(request.getNamaKebun());
        kebun.setLuasHektare(request.getLuasHektare());
        kebun.setKoordinat(request.getKoordinat());

        Kebun saved = kebunRepository.save(kebun);
        return toResponse(saved);
    }

    @Override
    public void deleteKebun(String kodeKebun) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isPresent()) {
            Kebun kebun = optionalKebun.get();
            if (kebun.getMandorId() != null) {
                throw new IllegalStateException("Cannot delete kebun with assigned mandor");
            }
            kebunRepository.deleteById(kodeKebun);
        }
    }

    private KebunResponse toResponse(Kebun kebun) {
        return new KebunResponse(
                kebun.getKodeKebun(),
                kebun.getNamaKebun(),
                kebun.getLuasHektare(),
                kebun.getKoordinat(),
                kebun.getMandorId(),
                kebun.getCreatedAt(),
                kebun.getSupirIds() != null ? kebun.getSupirIds() : List.of()
        );
    }
}
