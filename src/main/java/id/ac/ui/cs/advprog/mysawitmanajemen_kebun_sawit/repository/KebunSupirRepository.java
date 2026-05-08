package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.KebunSupir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KebunSupirRepository extends JpaRepository<KebunSupir, UUID> {
    List<KebunSupir> findByKodeKebun(String kodeKebun);
    void deleteByKodeKebun(String kodeKebun);
    void deleteByKodeKebunAndSupirId(String kodeKebun, UUID supirId);
}