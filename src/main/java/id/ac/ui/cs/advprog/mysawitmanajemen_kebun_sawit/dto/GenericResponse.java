package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public static <T> GenericResponse<T> success(String message, T data) {
        return new GenericResponse<>(200, message, data);
    }

    public static <T> GenericResponse<T> error(int statusCode, String message) {
        return new GenericResponse<>(statusCode, message, null);
    }
}
