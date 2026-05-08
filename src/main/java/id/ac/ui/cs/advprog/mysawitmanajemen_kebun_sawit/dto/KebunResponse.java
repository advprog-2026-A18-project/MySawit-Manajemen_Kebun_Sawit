package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KebunResponse {
    private String kodeKebun;
    private String namaKebun;
    private Integer luasHektare;
    private String koordinat;
    private UUID mandorId;
    private String namaMandor;
    private OffsetDateTime createdAt;
    private List<UUID> supirIds;
    private List<String> listSupir;
}
