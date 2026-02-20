package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KebunService {
    public List<Kebun> findAll();
    public Optional<Kebun> findById(UUID id);
    public Kebun create(Kebun kebun);
    public Kebun update(UUID id, Kebun kebun);
    public void delete(UUID id);
    public List<Kebun> searchByNama(String nama);
    public List<Kebun> findByLuas(Integer luas);
}
