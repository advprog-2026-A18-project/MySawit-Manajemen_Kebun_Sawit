package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service;

import java.util.UUID;

public interface UserRoleValidator {
    boolean isValidRole(UUID userId, String expectedRole);
    String getFullname(UUID userId);
}
