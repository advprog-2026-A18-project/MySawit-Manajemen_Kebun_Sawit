package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kebun")
public class Kebun {

    @Id
    @Column(name = "kode_kebun")
    private String kodeKebun;

    @Column(name = "nama_kebun", nullable = false)
    private String namaKebun;

    @Column(name = "luas_hektare", nullable = false)
    private Integer luasHektare;

    @Column(name = "koordinat", nullable = false)
    private String koordinat;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "mandor_id")
    private UUID mandorId;

    @Column(name = "mandor_nama")
    private String mandorNama;

    @OneToMany(mappedBy = "kodeKebun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KebunSupir> supirList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public void addSupir(UUID supirId, String namaSupir) {
        KebunSupir supir = new KebunSupir();
        supir.setKodeKebun(this.kodeKebun);
        supir.setSupirId(supirId);
        supir.setNamaSupir(namaSupir);
        supirList.add(supir);
    }

    public void removeSupir(UUID supirId) {
        supirList.removeIf(s -> s.getSupirId().equals(supirId));
    }

    public List<UUID> getSupirIds() {
        return supirList.stream().map(KebunSupir::getSupirId).toList();
    }

    public List<String> getSupirNamas() {
        return supirList.stream().map(KebunSupir::getNamaSupir).toList();
    }
}