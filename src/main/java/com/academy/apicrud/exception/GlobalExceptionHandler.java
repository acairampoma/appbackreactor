package com.academy.apicrud.exception;

import com.academy.apicrud.model.response.ResponseDataCrud;
import com.academy.apicrud.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ResponseDataCrud<Map<String, Object>>>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        log.error("Recurso no encontrado: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("resourceName", ex.getResourceName());
        details.put("fieldName", ex.getFieldName());
        details.put("fieldValue", ex.getFieldValue());
        details.put("timestamp", LocalDateTime.now());

        ResponseDataCrud<Map<String, Object>> response = new ResponseDataCrud<>(
                String.valueOf(Constants.HTTP_NOT_FOUND),
                ex.getMessage(),
                null,
                details
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ResponseDataCrud<Map<String, Object>>>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.error("Argumento ilegal: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("error", "Solicitud inválida");
        details.put("message", ex.getMessage());
        details.put("timestamp", LocalDateTime.now());

        ResponseDataCrud<Map<String, Object>> response = new ResponseDataCrud<>(
                String.valueOf(Constants.HTTP_BAD_REQUEST),
                ex.getMessage(),
                null,
                details
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ResponseDataCrud<Map<String, Object>>>> handleGenericException(Exception ex) {

        log.error("Error interno: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("error", "Error interno del servidor");
        details.put("timestamp", LocalDateTime.now());

        ResponseDataCrud<Map<String, Object>> response = new ResponseDataCrud<>(
                String.valueOf(Constants.HTTP_INTERNAL_SERVER_ERROR),
                "Error interno del servidor",
                null,
                details
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}