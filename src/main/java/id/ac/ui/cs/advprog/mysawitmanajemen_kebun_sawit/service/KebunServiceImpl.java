package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunSupirRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.util.GeoUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KebunServiceImpl implements KebunService {

    private final KebunRepository kebunRepository;
    private final KebunSupirRepository kebunSupirRepository;

    public KebunServiceImpl(KebunRepository kebunRepository, KebunSupirRepository kebunSupirRepository) {
        this.kebunRepository = kebunRepository;
        this.kebunSupirRepository = kebunSupirRepository;
    }

    @Override
    public List<KebunResponse> getAllKebun(String searchNama, String searchKode, String sortBy) {
        List<Kebun> kebunList;

        if (searchNama != null && !searchNama.isBlank()) {
            kebunList = kebunRepository.findByNamaKebunContainingIgnoreCase(searchNama);
        } else if (searchKode != null && !searchKode.isBlank()) {
            kebunList = kebunRepository.findByKodeKebunContainingIgnoreCase(searchKode);
        } else {
            if ("createdAt".equalsIgnoreCase(sortBy)) {
                kebunList = kebunRepository.findAllByOrderByCreatedAtDesc();
            } else {
                kebunList = kebunRepository.findAllByOrderByKodeKebunAsc();
            }
        }

        return kebunList.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public KebunResponse getKebunById(String kodeKebun, String searchSupirNama) {
        Kebun kebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));
        return toResponse(kebun, searchSupirNama);
    }

    @Override
    public KebunResponse createKebun(KebunRequest request) {
        if (request.getKoordinat() != null) {
            GeoUtils.validateKoordinat(request.getKoordinat());

            List<Kebun> existingKebuns = kebunRepository.findAll();
            for (Kebun existing : existingKebuns) {
                if (GeoUtils.isOverlapping(request.getKoordinat(), existing.getKoordinat())) {
                    throw new IllegalStateException("Kebun cannot overlap with existing kebun: " + existing.getNamaKebun());
                }
            }
        }

        Kebun kebun = new Kebun();
        kebun.setKodeKebun(request.getKodeKebun());
        kebun.setNamaKebun(request.getNamaKebun());
        kebun.setLuasHektare(request.getLuasHektare());
        kebun.setKoordinat(request.getKoordinat());

        Kebun saved = kebunRepository.save(kebun);
        return toResponse(saved);
    }

    @Override
    public KebunResponse updateKebun(String kodeKebun, KebunRequest request) {
        Kebun kebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));
        if (request.getNamaKebun() != null) {
            kebun.setNamaKebun(request.getNamaKebun());
        }
        if (request.getLuasHektare() != null) {
            kebun.setLuasHektare(request.getLuasHektare());
        }
        if (request.getKoordinat() != null) {
            GeoUtils.validateKoordinat(request.getKoordinat());
            kebun.setKoordinat(request.getKoordinat());
        }

        Kebun updated = kebunRepository.save(kebun);
        return toResponse(updated);
    }

    @Override
    public void deleteKebun(String kodeKebun) {
        Kebun kebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        if (kebun.getMandorId() != null) {
            throw new IllegalStateException("Cannot delete kebun with assigned mandor");
        }
        kebunSupirRepository.deleteByKodeKebun(kodeKebun);
        kebunRepository.deleteById(kodeKebun);
    }

    @Override
    public KebunResponse assignMandor(String kodeKebun, UUID mandorId, String mandorNama) {
        Kebun kebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        kebun.setMandorId(mandorId);
        kebun.setMandorNama(mandorNama);
        Kebun updated = kebunRepository.save(kebun);
        return toResponse(updated);
    }

    @Override
    public KebunResponse unassignMandor(String kodeKebun, String targetKebunKode) {
        if (targetKebunKode == null || targetKebunKode.isBlank()) {
            throw new IllegalArgumentException(
                    "Target kebun is required when unassigning mandor (mandatory reassignment)");
        }

        Kebun currentKebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        UUID currentMandorId = currentKebun.getMandorId();
        String currentMandorNama = currentKebun.getMandorNama();

        if (currentMandorId == null) {
            return toResponse(currentKebun);
        }

        Optional<Kebun> optionalTargetKebun = kebunRepository.findById(targetKebunKode);
        if (optionalTargetKebun.isEmpty()) {
            throw new IllegalArgumentException("Target kebun '" + targetKebunKode + "' not found");
        }

        currentKebun.setMandorId(null);
        currentKebun.setMandorNama(null);
        kebunRepository.save(currentKebun);

        Kebun targetKebun = optionalTargetKebun.get();
        targetKebun.setMandorId(currentMandorId);
        targetKebun.setMandorNama(currentMandorNama);
        kebunRepository.save(targetKebun);

        return toResponse(currentKebun);
    }

    @Override
    public KebunResponse assignSupir(String kodeKebun, UUID supirId, String namaSupir) {
        Kebun kebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        // Check if supir already assigned
        List<KebunSupir> existingSupir = kebunSupirRepository.findByKodeKebun(kodeKebun);
        boolean alreadyAssigned = existingSupir.stream()
                .anyMatch(s -> s.getSupirId().equals(supirId));
        if (alreadyAssigned) {
            return toResponse(kebun);
        }

        // Add new supir
        KebunSupir newSupir = new KebunSupir();
        newSupir.setKodeKebun(kodeKebun);
        newSupir.setSupirId(supirId);
        newSupir.setNamaSupir(namaSupir);
        kebunSupirRepository.save(newSupir);

        return toResponse(kebun);
    }

    @Override
    public KebunResponse unassignSupir(String kodeKebun, UUID supirId, String targetKebunKode) {
        if (targetKebunKode == null || targetKebunKode.isBlank()) {
            throw new IllegalArgumentException(
                    "Target kebun is required when unassigning supir (mandatory reassignment)");
        }

        Kebun currentKebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        // Remove from current kebun
        Optional<KebunSupir> supirToRemove = kebunSupirRepository.findByKodeKebun(kodeKebun).stream()
                .filter(s -> s.getSupirId().equals(supirId))
                .findFirst();
        String namaSupir = null;
        if (supirToRemove.isPresent()) {
            namaSupir = supirToRemove.get().getNamaSupir();
            kebunSupirRepository.delete(supirToRemove.get());
        }

        // Add to target kebun (mandatory reassignment)
        List<KebunSupir> targetSupirs = kebunSupirRepository.findByKodeKebun(targetKebunKode);
        boolean alreadyAssigned = targetSupirs.stream()
                .anyMatch(s -> s.getSupirId().equals(supirId));
        if (!alreadyAssigned) {
            KebunSupir supirToAdd = new KebunSupir();
            supirToAdd.setKodeKebun(targetKebunKode);
            supirToAdd.setSupirId(supirId);
            supirToAdd.setNamaSupir(namaSupir);
            kebunSupirRepository.save(supirToAdd);
        }

        return toResponse(currentKebun);
    }

    private KebunResponse toResponse(Kebun kebun, String searchSupirNama) {
        List<KebunSupir> supirList = kebunSupirRepository.findByKodeKebun(kebun.getKodeKebun()).stream()
                .filter(s -> searchSupirNama == null || searchSupirNama.isBlank() ||
                        (s.getNamaSupir() != null && s.getNamaSupir().toLowerCase().contains(searchSupirNama.toLowerCase())))
                .toList();

        List<UUID> supirIds = supirList.stream().map(KebunSupir::getSupirId).toList();
        List<String> namaSupirs = supirList.stream().map(KebunSupir::getNamaSupir).toList();

        return new KebunResponse(
                kebun.getKodeKebun(),
                kebun.getNamaKebun(),
                kebun.getLuasHektare(),
                kebun.getKoordinat(),
                kebun.getMandorId(),
                kebun.getMandorNama(),
                kebun.getCreatedAt(),
                supirIds,
                namaSupirs // listSupir
        );
    }

    private KebunResponse toResponse(Kebun kebun) {
        return toResponse(kebun, null);
    }
}