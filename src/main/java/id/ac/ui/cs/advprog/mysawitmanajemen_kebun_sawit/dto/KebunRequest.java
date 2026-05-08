package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KebunRequest {
    private String kodeKebun;
    private String namaKebun;
    private Integer luasHektare;
    private String koordinat;
}