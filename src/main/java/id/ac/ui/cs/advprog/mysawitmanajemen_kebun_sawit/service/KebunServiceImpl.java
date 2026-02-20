package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class KebunServiceImpl implements KebunService {
    private final KebunRepository repository;

    @Override
    public List<Kebun> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Kebun> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Kebun create(Kebun kebun) {
        return repository.save(kebun);
    }

    @Override
    public Kebun update(UUID id, Kebun kebun) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<Kebun> searchByNama(String nama) {
        return repository.findByNamaKebun(nama);
    }

    @Override
    public List<Kebun> findByLuas(Integer luas) {
        return repository.findByLuasHektare(luas);
    }
}
