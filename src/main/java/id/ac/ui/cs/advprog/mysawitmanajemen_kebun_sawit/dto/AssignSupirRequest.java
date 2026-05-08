package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignSupirRequest {
    private UUID supirId;
    private String namaSupir; // dari frontend
    private UUID targetKebunKode;
}