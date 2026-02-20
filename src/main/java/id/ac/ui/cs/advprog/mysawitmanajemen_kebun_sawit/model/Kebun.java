package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kebun")
public class Kebun {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nama_kebun", nullable = false)
    private String namaKebun;

    @Column(name = "luas_hektare", nullable = false)
    private Integer luasHektare;

    @Column(name = "koordinat", columnDefinition = "jsonb", nullable = false)
    private String koordinat;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
