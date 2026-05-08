package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.KebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunSupirRepository;
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
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        Kebun kebun = optionalKebun.get();
        return toResponse(kebun, searchSupirNama);
    }

    @Override
    public KebunResponse createKebun(KebunRequest request) {
        if (request.getKoordinat() != null) {
            List<Kebun> existingKebuns = kebunRepository.findAll();
            for (Kebun existing : existingKebuns) {
                if (isOverlapping(request.getKoordinat(), existing.getKoordinat())) {
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

    private boolean isOverlapping(String coord1, String coord2) {
        if (coord1 == null || coord2 == null) {
            return false;
        }

        double[] bounds1 = parseKoordinat(coord1);
        double[] bounds2 = parseKoordinat(coord2);

        double minX1 = bounds1[0];
        double maxX1 = bounds1[1];
        double minY1 = bounds1[2];
        double maxY1 = bounds1[3];
        double minX2 = bounds2[0];
        double maxX2 = bounds2[1];
        double minY2 = bounds2[2];
        double maxY2 = bounds2[3];

        boolean overlapX = maxX1 > minX2 && minX1 < maxX2;
        boolean overlapY = maxY1 > minY2 && minY1 < maxY2;

        return overlapX && overlapY;
    }

    private double[] parseKoordinat(String coordJson) {
        try {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"lat\":\\s*([\\d.]+).*?\"lng\":\\s*([\\d.]+)");
            java.util.regex.Matcher matcher = pattern.matcher(coordJson);

            while (matcher.find()) {
                double lat = Double.parseDouble(matcher.group(1));
                double lng = Double.parseDouble(matcher.group(2));
                minX = Math.min(minX, lat);
                maxX = Math.max(maxX, lat);
                minY = Math.min(minY, lng);
                maxY = Math.max(maxY, lng);
            }

            return new double[]{minX, maxX, minY, maxY};
        } catch (Exception e) {
            return new double[]{0, 0, 0, 0};
        }
    }

    @Override
    public KebunResponse updateKebun(String kodeKebun, KebunRequest request) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        Kebun kebun = optionalKebun.get();
        if (request.getNamaKebun() != null) {
            kebun.setNamaKebun(request.getNamaKebun());
        }
        if (request.getLuasHektare() != null) {
            kebun.setLuasHektare(request.getLuasHektare());
        }
        if (request.getKoordinat() != null) {
            kebun.setKoordinat(request.getKoordinat());
        }

        Kebun updated = kebunRepository.save(kebun);
        return toResponse(updated);
    }

    @Override
    public void deleteKebun(String kodeKebun) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isPresent()) {
            Kebun kebun = optionalKebun.get();
            if (kebun.getMandorId() != null) {
                throw new IllegalStateException("Cannot delete kebun with assigned mandor");
            }
            kebunSupirRepository.deleteByKodeKebun(kodeKebun);
            kebunRepository.deleteById(kodeKebun);
        }
    }

    @Override
    public KebunResponse assignMandor(String kodeKebun, UUID mandorId, String mandorNama) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        Kebun kebun = optionalKebun.get();
        kebun.setMandorId(mandorId);
        kebun.setMandorNama(mandorNama);
        Kebun updated = kebunRepository.save(kebun);
        return toResponse(updated);
    }

    @Override
    public KebunResponse unassignMandor(String kodeKebun, String targetKebunKode) {
        Optional<Kebun> optionalCurrentKebun = kebunRepository.findById(kodeKebun);
        if (optionalCurrentKebun.isEmpty()) {
            return null;
        }

        Kebun currentKebun = optionalCurrentKebun.get();
        UUID currentMandorId = currentKebun.getMandorId();
        String currentMandorNama = currentKebun.getMandorNama();

        if (currentMandorId == null) {
            return toResponse(currentKebun);
        }

        currentKebun.setMandorId(null);
        currentKebun.setMandorNama(null);
        kebunRepository.save(currentKebun);

        if (targetKebunKode != null) {
            Optional<Kebun> optionalTargetKebun = kebunRepository.findById(targetKebunKode);
            if (optionalTargetKebun.isPresent()) {
                Kebun targetKebun = optionalTargetKebun.get();
                targetKebun.setMandorId(currentMandorId);
                targetKebun.setMandorNama(currentMandorNama);
                kebunRepository.save(targetKebun);
            }
        }

        return toResponse(currentKebun);
    }

    @Override
    public KebunResponse assignSupir(String kodeKebun, UUID supirId, String namaSupir) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        // Check if supir already assigned
        List<KebunSupir> existingSupir = kebunSupirRepository.findByKodeKebun(kodeKebun);
        boolean alreadyAssigned = existingSupir.stream()
                .anyMatch(s -> s.getSupirId().equals(supirId));
        if (alreadyAssigned) {
            return toResponse(optionalKebun.get());
        }

        // Add new supir
        KebunSupir newSupir = new KebunSupir();
        newSupir.setKodeKebun(kodeKebun);
        newSupir.setSupirId(supirId);
        newSupir.setNamaSupir(namaSupir);
        kebunSupirRepository.save(newSupir);

        return toResponse(optionalKebun.get());
    }

    @Override
    public KebunResponse unassignSupir(String kodeKebun, UUID supirId, String targetKebunKode) {
        Optional<Kebun> optionalCurrentKebun = kebunRepository.findById(kodeKebun);
        if (optionalCurrentKebun.isEmpty()) {
            return null;
        }

        // Remove from current kebun
        Optional<KebunSupir> supirToRemove = kebunSupirRepository.findByKodeKebun(kodeKebun).stream()
                .filter(s -> s.getSupirId().equals(supirId))
                .findFirst();
        String namaSupir = null;
        if (supirToRemove.isPresent()) {
            namaSupir = supirToRemove.get().getNamaSupir();
            kebunSupirRepository.delete(supirToRemove.get());
        }

        // Add to target kebun
        if (targetKebunKode != null) {
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
        }

        return toResponse(optionalCurrentKebun.get());
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