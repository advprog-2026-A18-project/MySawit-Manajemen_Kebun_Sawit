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
    private final UserRoleValidator userRoleValidator;

    public KebunServiceImpl(
            KebunRepository kebunRepository,
            KebunSupirRepository kebunSupirRepository,
            UserRoleValidator userRoleValidator
    ) {
        this.kebunRepository = kebunRepository;
        this.kebunSupirRepository = kebunSupirRepository;
        this.userRoleValidator = userRoleValidator;
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
        validateCreateRequest(request);

        if (kebunRepository.existsById(request.getKodeKebun())) {
            throw new IllegalStateException(
                    "Kebun with kode '" + request.getKodeKebun() + "' already exists");
        }

        GeoUtils.validateKoordinat(request.getKoordinat());
        validateNoOverlap(request.getKoordinat(), null);

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
            if (request.getNamaKebun().isBlank()) {
                throw new IllegalArgumentException("Nama kebun cannot be blank");
            }
            kebun.setNamaKebun(request.getNamaKebun());
        }
        if (request.getLuasHektare() != null) {
            if (request.getLuasHektare() <= 0) {
                throw new IllegalArgumentException("Luas hektare must be greater than 0");
            }
            kebun.setLuasHektare(request.getLuasHektare());
        }
        if (request.getKoordinat() != null) {
            GeoUtils.validateKoordinat(request.getKoordinat());
            validateNoOverlap(request.getKoordinat(), kodeKebun);
            kebun.setKoordinat(request.getKoordinat());
        }

        Kebun updated = kebunRepository.save(kebun);
        return toResponse(updated);
    }

    private void validateCreateRequest(KebunRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getKodeKebun() == null || request.getKodeKebun().isBlank()) {
            throw new IllegalArgumentException("Kode kebun is required");
        }
        if (request.getNamaKebun() == null || request.getNamaKebun().isBlank()) {
            throw new IllegalArgumentException("Nama kebun is required");
        }
        if (request.getLuasHektare() == null || request.getLuasHektare() <= 0) {
            throw new IllegalArgumentException("Luas hektare must be greater than 0");
        }
        if (request.getKoordinat() == null || request.getKoordinat().isBlank()) {
            throw new IllegalArgumentException("Koordinat is required");
        }
    }

    private void validateNoOverlap(String koordinat, String excludeKodeKebun) {
        List<Kebun> existingKebuns = kebunRepository.findAll();
        for (Kebun existing : existingKebuns) {
            if (excludeKodeKebun != null && excludeKodeKebun.equals(existing.getKodeKebun())) {
                continue;
            }
            if (GeoUtils.isOverlapping(koordinat, existing.getKoordinat())) {
                throw new IllegalStateException(
                        "Kebun cannot overlap with existing kebun: " + existing.getNamaKebun());
            }
        }
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
        validateUserRole(mandorId, "MANDOR", "Mandor");
        String verifiedMandorNama = resolveFullname(mandorId, mandorNama);

        Kebun kebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        if (kebun.getMandorId() != null && !kebun.getMandorId().equals(mandorId)) {
            throw new IllegalStateException(
                    "Kebun '" + kodeKebun
                            + "' already has a mandor assigned. Unassign the current mandor first.");
        }

        kebun.setMandorId(mandorId);
        kebun.setMandorNama(verifiedMandorNama);
        Kebun updated = kebunRepository.save(kebun);
        return toResponse(updated);
    }

    @Override
    public KebunResponse unassignMandor(String kodeKebun, String targetKebunKode) {
        if (targetKebunKode == null || targetKebunKode.isBlank()) {
            throw new IllegalArgumentException(
                    "Target kebun is required when unassigning mandor (mandatory reassignment)");
        }

        if (targetKebunKode.equals(kodeKebun)) {
            throw new IllegalArgumentException(
                    "Target kebun must be different from the source kebun");
        }

        Kebun currentKebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        UUID currentMandorId = currentKebun.getMandorId();
        String currentMandorNama = currentKebun.getMandorNama();

        if (currentMandorId == null) {
            return toResponse(currentKebun);
        }

        Kebun targetKebun = kebunRepository.findById(targetKebunKode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Target kebun '" + targetKebunKode + "' not found"));

        if (targetKebun.getMandorId() != null) {
            throw new IllegalStateException(
                    "Target kebun '" + targetKebunKode
                            + "' already has a mandor assigned. Unassign that mandor first.");
        }

        currentKebun.setMandorId(null);
        currentKebun.setMandorNama(null);
        kebunRepository.save(currentKebun);

        targetKebun.setMandorId(currentMandorId);
        targetKebun.setMandorNama(currentMandorNama);
        kebunRepository.save(targetKebun);

        return toResponse(currentKebun);
    }

    @Override
    public KebunResponse assignSupir(String kodeKebun, UUID supirId, String namaSupir) {
        validateUserRole(supirId, "SUPIR", "Supir");
        String verifiedNamaSupir = resolveFullname(supirId, namaSupir);

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
        newSupir.setNamaSupir(verifiedNamaSupir);
        kebunSupirRepository.save(newSupir);

        return toResponse(kebun);
    }

    @Override
    public KebunResponse unassignSupir(String kodeKebun, UUID supirId, String targetKebunKode) {
        if (targetKebunKode == null || targetKebunKode.isBlank()) {
            throw new IllegalArgumentException(
                    "Target kebun is required when unassigning supir (mandatory reassignment)");
        }

        if (targetKebunKode.equals(kodeKebun)) {
            throw new IllegalArgumentException(
                    "Target kebun must be different from the source kebun");
        }

        Kebun currentKebun = kebunRepository.findById(kodeKebun)
                .orElseThrow(() -> new KebunNotFoundException(kodeKebun));

        if (!kebunRepository.existsById(targetKebunKode)) {
            throw new IllegalArgumentException(
                    "Target kebun '" + targetKebunKode + "' not found");
        }

        // Remove from current kebun
        Optional<KebunSupir> supirToRemove = kebunSupirRepository.findByKodeKebun(kodeKebun).stream()
                .filter(s -> s.getSupirId().equals(supirId))
                .findFirst();

        if (supirToRemove.isEmpty()) {
            throw new IllegalArgumentException(
                    "Supir '" + supirId + "' is not assigned to kebun '" + kodeKebun + "'");
        }

        String namaSupir = supirToRemove.get().getNamaSupir();
        kebunSupirRepository.delete(supirToRemove.get());

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

    private void validateUserRole(UUID userId, String expectedRole, String label) {
        if (!userRoleValidator.isValidRole(userId, expectedRole)) {
            throw new IllegalArgumentException(label + " tidak ditemukan atau role tidak valid.");
        }
    }

    private String resolveFullname(UUID userId, String fallbackName) {
        String fullname = userRoleValidator.getFullname(userId);
        return fullname == null || fullname.isBlank() ? fallbackName : fullname;
    }
}
