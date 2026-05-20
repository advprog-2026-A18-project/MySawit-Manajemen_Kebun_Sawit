package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.exception;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KebunNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GenericResponse<Void> handleKebunNotFound(KebunNotFoundException ex) {
        return GenericResponse.error(404, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericResponse<Void> handleIllegalState(IllegalStateException ex) {
        return GenericResponse.error(400, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return GenericResponse.error(400, ex.getMessage());
    }
}
