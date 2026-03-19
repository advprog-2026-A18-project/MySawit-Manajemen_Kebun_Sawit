package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KebunResponse {
    private String kodeKebun;
    private String namaKebun;
    private Integer luasHektare;
    private String koordinat;
    private String mandorId;
    private OffsetDateTime createdAt;
}
