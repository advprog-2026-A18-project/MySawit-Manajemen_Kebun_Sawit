package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KebunRepository extends JpaRepository<Kebun, UUID> {
    List<Kebun> findByNamaKebun(String nama);
    List<Kebun> findByLuasHektare(Integer luas);
}
