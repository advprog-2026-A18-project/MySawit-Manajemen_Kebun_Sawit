package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kebun_supir")
public class KebunSupir {

    @Id
    @Column(name = "supir_id")
    private UUID supirId;

    @Column(name = "kode_kebun")
    private String kodeKebun;

    @Column(name = "supir_nama")
    private String namaSupir;
}