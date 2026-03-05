package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "koordinat", columnDefinition = "jsonb", nullable = false)
    private String koordinat;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "mandor_id")
    private String mandorId;

    @ElementCollection
    @CollectionTable(name = "kebun_supir", joinColumns = @JoinColumn(name = "kode_kebun"))
    @Column(name = "supir_id")
    private List<String> supirIds = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
