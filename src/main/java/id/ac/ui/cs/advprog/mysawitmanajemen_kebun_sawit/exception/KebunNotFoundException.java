package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.exception;

public class KebunNotFoundException extends RuntimeException {

    public KebunNotFoundException(String kodeKebun) {
        super("Kebun with kode '" + kodeKebun + "' not found");
    }
}
