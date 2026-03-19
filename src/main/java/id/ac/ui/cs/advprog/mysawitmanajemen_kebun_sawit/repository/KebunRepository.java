package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KebunRepository extends JpaRepository<Kebun, String> {

    List<Kebun> findByNamaKebunContainingIgnoreCase(String nama);

    List<Kebun> findByKodeKebunContainingIgnoreCase(String kode);

    Optional<Kebun> findByMandorId(String mandorId);

    List<Kebun> findAllByOrderByCreatedAtDesc();
}
