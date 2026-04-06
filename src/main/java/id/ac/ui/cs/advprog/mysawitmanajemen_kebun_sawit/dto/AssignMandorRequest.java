package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignMandorRequest {
    private String mandorId;
    private String targetKebunKode; // wajib assign ke kebun lain
}