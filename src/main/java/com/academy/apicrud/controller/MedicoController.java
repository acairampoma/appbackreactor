package com.academy.apicrud.controller;

import com.academy.apicrud.exception.ResourceNotFoundException;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.dto.PageResponseDto;
import com.academy.apicrud.model.response.ResponseDataCrud;
import com.academy.apicrud.model.response.ResponseMedico;
import com.academy.apicrud.service.MedicoService;
import com.academy.apicrud.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Médicos", description = "API para gestión de médicos")
public class MedicoController {

    private final MedicoService medicoService;

    @GetMapping("/page")
    @Operation(summary = "Obtener médicos paginados y filtrados")
    @ApiResponse(responseCode = "200", description = "Lista paginada de médicos",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    public Mono<ResponseEntity<ResponseDataCrud<PageResponseDto<MedicoDto>>>> getMedicosPaginados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long especialidadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        log.info("REST request para obtener médicos paginados: page={}, size={}, sortBy={}, sortOrder={}, nombre={}, especialidadId={}",
                page, size, sortBy, sortOrder, nombre, especialidadId);

        try {
            // Validar parámetros de ordenamiento
            medicoService.validateSortParameters(sortBy, sortOrder);

            // Crear objeto Pageable
            Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // Obtener médicos paginados
            return medicoService.getMedicosPaginados(nombre, especialidadId, pageable)
                    .map(pageResponse -> {
                        // Usar el número de elementos en la lista como totalrows
                        int elementCount = pageResponse.getContent().size();

                        ResponseDataCrud<PageResponseDto<MedicoDto>> response = new ResponseDataCrud<>(
                                String.valueOf(Constants.HTTP_OK),
                                Constants.GET,
                                elementCount,  // Número correcto de elementos
                                pageResponse
                        );
                        return ResponseEntity.ok(response);
                    })
                    .onErrorResume(e -> {
                        if (e instanceof IllegalArgumentException) {
                            log.error("Error de validación en parámetros de paginación: {}", e.getMessage());
                            ResponseDataCrud<PageResponseDto<MedicoDto>> response = new ResponseDataCrud<>(
                                    String.valueOf(Constants.HTTP_BAD_REQUEST),
                                    e.getMessage(),
                                    null,
                                    null
                            );
                            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
                        }
                        log.error("Error al obtener médicos paginados: {}", e.getMessage());
                        ResponseDataCrud<PageResponseDto<MedicoDto>> response = new ResponseDataCrud<>(
                                String.valueOf(Constants.HTTP_INTERNAL_SERVER_ERROR),
                                "Error al obtener médicos paginados: " + e.getMessage(),
                                null,
                                null
                        );
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                    });
        } catch (IllegalArgumentException e) {
            log.error("Error de validación en parámetros de paginación: {}", e.getMessage());
            ResponseDataCrud<PageResponseDto<MedicoDto>> response = new ResponseDataCrud<>(
                    String.valueOf(Constants.HTTP_BAD_REQUEST),
                    e.getMessage(),
                    null,
                    null
            );
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener todos los médicos")
    @ApiResponse(responseCode = "200", description = "Lista de médicos",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    public Mono<ResponseEntity<ResponseDataCrud<List<MedicoDto>>>> getAllMedicos() {
        log.info("REST request para obtener todos los médicos");
        return medicoService.getAllMedicos()
                .collectList()
                .map(medicosDto -> {
                    ResponseDataCrud<List<MedicoDto>> response = new ResponseDataCrud<>(
                            String.valueOf(Constants.HTTP_OK),
                            Constants.GET,
                            medicosDto.size(),
                            medicosDto
                    );
                    return ResponseEntity.ok(response);
                });
    }

    @GetMapping(value = "/with-especialidad", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener todos los médicos con su especialidad")
    @ApiResponse(responseCode = "200", description = "Lista de médicos con especialidad",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    public Mono<ResponseEntity<ResponseDataCrud<List<ResponseMedico>>>> getAllMedicosWithEspecialidad() {
        log.info("REST request para obtener todos los médicos con especialidad");
        return medicoService.getAllMedicosWithEspecialidad()
                .collectList()
                .map(medicosResponse -> {
                    ResponseDataCrud<List<ResponseMedico>> response = new ResponseDataCrud<>(
                            String.valueOf(Constants.HTTP_OK),
                            Constants.GET,
                            medicosResponse.size(),
                            medicosResponse
                    );
                    return ResponseEntity.ok(response);
                });
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener un médico por ID")
    @ApiResponse(responseCode = "200", description = "Médico encontrado",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    public Mono<ResponseEntity<ResponseDataCrud<MedicoDto>>> getMedicoById(@PathVariable Long id) {
        log.info("REST request para obtener médico con ID: {}", id);
        return medicoService.getMedicoById(id)
                .map(medicoDto -> {
                    ResponseDataCrud<MedicoDto> response = new ResponseDataCrud<>(
                            String.valueOf(Constants.HTTP_OK),
                            Constants.GET,
                            1,
                            medicoDto
                    );
                    return ResponseEntity.ok(response);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Médico", "id", id)));
    }

    @GetMapping(value = "/{id}/with-especialidad", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener un médico con su especialidad por ID")
    @ApiResponse(responseCode = "200", description = "Médico con especialidad encontrado",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    public Mono<ResponseEntity<ResponseDataCrud<ResponseMedico>>> getMedicoWithEspecialidadById(@PathVariable Long id) {
        log.info("REST request para obtener médico con especialidad, ID: {}", id);
        return medicoService.getMedicoWithEspecialidadById(id)
                .map(responseMedico -> {
                    ResponseDataCrud<ResponseMedico> response = new ResponseDataCrud<>(
                            String.valueOf(Constants.HTTP_OK),
                            Constants.GET,
                            1,
                            responseMedico
                    );
                    return ResponseEntity.ok(response);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Médico", "id", id)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo médico")
    @ApiResponse(responseCode = "201", description = "Médico creado",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    @ApiResponse(responseCode = "400", description = "Datos de médico inválidos")
    public Mono<ResponseEntity<ResponseDataCrud<MedicoDto>>> createMedico(@Valid @RequestBody MedicoDto medicoDto) {
        log.info("REST request para crear un nuevo médico: {}", medicoDto);
        return medicoService.saveMedico(medicoDto)
                .map(medicoGuardado -> {
                    ResponseDataCrud<MedicoDto> response = new ResponseDataCrud<>(
                            String.valueOf(Constants.HTTP_CREATED),
                            Constants.POST,
                            null,
                            medicoGuardado
                    );
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar un médico existente")
    @ApiResponse(responseCode = "200", description = "Médico actualizado",
            content = @Content(schema = @Schema(implementation = ResponseDataCrud.class)))
    @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    @ApiResponse(responseCode = "400", description = "Datos de médico inválidos")
    public Mono<ResponseEntity<ResponseDataCrud<MedicoDto>>> updateMedico(@PathVariable Long id, @Valid @RequestBody MedicoDto medicoDto) {
        log.info("REST request para actualizar médico con ID: {}", id);
        return medicoService.updateMedico(id, medicoDto)
                .map(medicoActualizado -> {
                    ResponseDataCrud<MedicoDto> response = new ResponseDataCrud<>(
                            String.valueOf(Constants.HTTP_OK),
                            Constants.PUT,
                            null,
                            medicoActualizado
                    );
                    return ResponseEntity.ok(response);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Médico", "id", id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un médico")
    @ApiResponse(responseCode = "204", description = "Médico eliminado")
    @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    public Mono<ResponseEntity<ResponseDataCrud<Object>>> deleteMedico(@PathVariable Long id) {
        log.info("REST request para eliminar médico con ID: {}", id);
        return medicoService.getMedicoById(id)
                .flatMap(medico -> medicoService.deleteMedico(id)
                        .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .body(new ResponseDataCrud<>(
                                        String.valueOf(Constants.HTTP_NO_CONTENT),
                                        Constants.DELETE,
                                        null,
                                        null
                                )))))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Médico", "id", id)));
    }
}