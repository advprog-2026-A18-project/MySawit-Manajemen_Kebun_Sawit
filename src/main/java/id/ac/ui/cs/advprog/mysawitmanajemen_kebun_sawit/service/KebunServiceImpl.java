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
    public KebunResponse getKebunById(String kodeKebun, String searchSupirNama) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        Kebun kebun = optionalKebun.get();
        KebunResponse response = toResponse(kebun);

        // Filter supir by nama (search term)
        if (searchSupirNama != null && !searchSupirNama.isBlank() && response.getSupirIds() != null) {
            List<String> filteredSupirs = response.getSupirIds().stream()
                .filter(supirId -> supirId.toLowerCase().contains(searchSupirNama.toLowerCase()))
                .collect(Collectors.toList());
            response.setSupirIds(filteredSupirs);
        }

        return response;
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

        double minX1 = bounds1[0], maxX1 = bounds1[1], minY1 = bounds1[2], maxY1 = bounds1[3];
        double minX2 = bounds2[0], maxX2 = bounds2[1], minY2 = bounds2[2], maxY2 = bounds2[3];

        boolean overlapX = maxX1 > minX2 && minX1 < maxX2;
        boolean overlapY = maxY1 > minY2 && minY1 < maxY2;

        return overlapX && overlapY;
    }

    private double[] parseKoordinat(String coordJson) {
        try {
            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

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
            kebunRepository.deleteById(kodeKebun);
        }
    }

    @Override
    public KebunResponse assignMandor(String kodeKebun, String mandorId) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        Kebun kebun = optionalKebun.get();
        kebun.setMandorId(mandorId);
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
        String currentMandorId = currentKebun.getMandorId();

        if (currentMandorId == null) {
            return toResponse(currentKebun);
        }

        currentKebun.setMandorId(null);
        kebunRepository.save(currentKebun);

        if (targetKebunKode != null && !targetKebunKode.isBlank()) {
            Optional<Kebun> optionalTargetKebun = kebunRepository.findById(targetKebunKode);
            if (optionalTargetKebun.isPresent()) {
                Kebun targetKebun = optionalTargetKebun.get();
                targetKebun.setMandorId(currentMandorId);
                kebunRepository.save(targetKebun);
            }
        }

        return toResponse(currentKebun);
    }

    @Override
    public KebunResponse assignSupir(String kodeKebun, String supirId) {
        Optional<Kebun> optionalKebun = kebunRepository.findById(kodeKebun);
        if (optionalKebun.isEmpty()) {
            return null;
        }

        Kebun kebun = optionalKebun.get();
        if (kebun.getSupirIds() == null) {
            kebun.setSupirIds(new java.util.ArrayList<>());
        }

        if (!kebun.getSupirIds().contains(supirId)) {
            kebun.getSupirIds().add(supirId);
            Kebun updated = kebunRepository.save(kebun);
            return toResponse(updated);
        }
        return toResponse(kebun);
    }

    @Override
    public KebunResponse unassignSupir(String kodeKebun, String supirId, String targetKebunKode) {
        Optional<Kebun> optionalCurrentKebun = kebunRepository.findById(kodeKebun);
        if (optionalCurrentKebun.isEmpty()) {
            return null;
        }

        Kebun currentKebun = optionalCurrentKebun.get();
        if (currentKebun.getSupirIds() != null && currentKebun.getSupirIds().contains(supirId)) {
            currentKebun.getSupirIds().remove(supirId);
            kebunRepository.save(currentKebun);
        }

        if (targetKebunKode != null && !targetKebunKode.isBlank()) {
            Optional<Kebun> optionalTargetKebun = kebunRepository.findById(targetKebunKode);
            if (optionalTargetKebun.isPresent()) {
                Kebun targetKebun = optionalTargetKebun.get();
                if (targetKebun.getSupirIds() == null) {
                    targetKebun.setSupirIds(new java.util.ArrayList<>());
                }
                if (!targetKebun.getSupirIds().contains(supirId)) {
                    targetKebun.getSupirIds().add(supirId);
                    kebunRepository.save(targetKebun);
                }
            }
        }

        return toResponse(currentKebun);
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
