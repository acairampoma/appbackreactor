package com.academy.apicrud.service.impl;

import com.academy.apicrud.exception.ResourceNotFoundException;
import com.academy.apicrud.mapper.IMedicoMapper;
import com.academy.apicrud.model.domain.Medico;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.dto.PageResponseDto;
import com.academy.apicrud.model.response.ResponseMedico;
import com.academy.apicrud.repository.MedicoRepository;
import com.academy.apicrud.service.EspecialidadService;
import com.academy.apicrud.service.MedicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadService especialidadService;
    private final IMedicoMapper medicoMapper;

    // Campos válidos para ordenar
    private static final Set<String> VALID_SORT_FIELDS = new HashSet<>(Arrays.asList(
            "id", "nombre", "especialidadId"
    ));

    @Override
    public Flux<ResponseMedico> getAllMedicosWithEspecialidad() {
        log.info("Obteniendo todos los médicos con su especialidad");
        return medicoRepository.findAllMedicoWithEspecialidad()
                .doOnComplete(() -> log.info("Consulta de médicos con especialidad completada"))
                .onErrorResume(error -> {
                    log.error("Error al obtener médicos con especialidad: {}", error.getMessage());
                    return Flux.empty();
                });
    }

    @Override
    public Mono<ResponseMedico> getMedicoWithEspecialidadById(Long id) {
        log.info("Buscando médico con especialidad, ID: {}", id);
        return Mono.just(id)
                .filter(Objects::nonNull)
                .flatMap(medicoRepository::findMedicoWithEspecialidadById)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No se encontró médico con especialidad para el ID: {}", id);
                    return Mono.empty();
                }))
                .doOnSuccess(medico -> {
                    if (medico != null) {
                        log.info("Médico con especialidad encontrado: {}", medico);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error al buscar médico con especialidad, ID {}: {}", id, error.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Flux<MedicoDto> getAllMedicos() {
        log.info("Obteniendo todos los médicos");
        return medicoRepository.findAll()
                .map(medicoMapper::toDto)
                .doOnComplete(() -> log.info("Consulta de todos los médicos completada"))
                .onErrorResume(error -> {
                    log.error("Error al obtener todos los médicos: {}", error.getMessage());
                    return Flux.empty();
                });
    }

    @Override
    public Mono<MedicoDto> getMedicoById(Long id) {
        log.info("Buscando médico con ID: {}", id);
        return Mono.just(id)
                .filter(Objects::nonNull)
                .flatMap(medicoRepository::findById)
                .map(medicoMapper::toDto)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No se encontró médico con ID: {}", id);
                    return Mono.empty();
                }))
                .doOnSuccess(medico -> {
                    if (medico != null) {
                        log.info("Médico encontrado: {}", medico);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error al buscar médico con ID {}: {}", id, error.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    @Transactional
    public Mono<MedicoDto> saveMedico(MedicoDto medicoDto) {
        log.info("Guardando nuevo médico: {}", medicoDto);

        // Verificar si el DTO es nulo antes de crear el Mono
        if (medicoDto == null) {
            log.error("Error al guardar médico: El médico no puede ser nulo");
            return Mono.error(new IllegalArgumentException("El médico no puede ser nulo"));
        }

        return Mono.just(medicoDto)
                .filter(this::validateMedicoDto)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Datos de médico inválidos")))
                .flatMap(validMedicoDto -> {
                    Medico medico = medicoMapper.toEntity(validMedicoDto);
                    // Asegurar que el ID sea nulo para nuevas entidades
                    medico.setId(null);

                    // Verificar que la especialidad existe
                    return especialidadService.existsById(medico.getEspecialidadId())
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.error(new ResourceNotFoundException(
                                            "Especialidad", "id", medico.getEspecialidadId()));
                                }
                                return medicoRepository.save(medico)
                                        .map(medicoMapper::toDto);
                            });
                })
                .doOnSuccess(saved -> log.info("Médico guardado con éxito: {}", saved))
                .onErrorResume(error -> {
                    log.error("Error al guardar médico: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    @Override
    @Transactional
    public Mono<MedicoDto> updateMedico(Long id, MedicoDto medicoDto) {
        log.info("Actualizando médico con ID: {}", id);

        // Verificar si el ID o el DTO son nulos antes de crear el Mono
        if (id == null || medicoDto == null) {
            log.error("Error al actualizar médico: Médico o ID no pueden ser nulos");
            return Mono.error(new IllegalArgumentException("Médico o ID no pueden ser nulos"));
        }

        return Mono.just(medicoDto)
                .filter(this::validateMedicoDto)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Datos de médico inválidos")))
                .flatMap(validMedicoDto -> {
                    Medico medico = medicoMapper.toEntity(validMedicoDto);

                    // Verificar que la especialidad existe
                    return especialidadService.existsById(medico.getEspecialidadId())
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.error(new ResourceNotFoundException(
                                            "Especialidad", "id", medico.getEspecialidadId()));
                                }

                                return medicoRepository.findById(id)
                                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Médico", "id", id)))
                                        .flatMap(existingMedico -> {
                                            medico.setId(id);
                                            return medicoRepository.save(medico)
                                                    .map(medicoMapper::toDto);
                                        });
                            });
                })
                .doOnSuccess(updated -> log.info("Médico actualizado con éxito: {}", updated))
                .onErrorResume(error -> {
                    log.error("Error al actualizar médico con ID {}: {}", id, error.getMessage());
                    return Mono.error(error);
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteMedico(Long id) {
        log.info("Eliminando médico con ID: {}", id);

        // Verificar si el ID es nulo antes de crear el Mono
        if (id == null) {
            log.error("Error al eliminar médico: El ID no puede ser nulo");
            return Mono.error(new IllegalArgumentException("El ID no puede ser nulo"));
        }

        return medicoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Médico", "id", id)))
                .flatMap(medico -> {
                    log.info("Médico encontrado para eliminar: {}", medico);
                    return medicoRepository.deleteById(id);
                })
                .doOnSuccess(v -> log.info("Médico con ID {} eliminado con éxito", id))
                .onErrorResume(error -> {
                    log.error("Error al eliminar médico con ID {}: {}", id, error.getMessage());
                    if (error instanceof ResourceNotFoundException) {
                        return Mono.error(error);
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<PageResponseDto<MedicoDto>> getMedicosPaginados(String nombre, Long especialidadId, Pageable pageable) {
        // Validar que pageable no sea nulo
        if (pageable == null) {
            log.error("Error al obtener médicos paginados: Pageable no puede ser nulo");
            return Mono.error(new IllegalArgumentException("Pageable no puede ser nulo"));
        }

        log.info("Obteniendo médicos paginados: página={}, tamaño={}, ordenamiento={}, nombre={}, especialidadId={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort(), nombre, especialidadId);

        int size = pageable.getPageSize();
        long offset = pageable.getOffset();

        // Seleccionar el método apropiado según los parámetros de ordenamiento
        String sortBy = getSortProperty(pageable);
        boolean isAscending = isAscendingSort(pageable);

        // Seleccionar el método de repositorio apropiado según los parámetros
        Flux<Medico> medicosFlux;
        if ((nombre == null || nombre.isEmpty()) && especialidadId == null) {
            // Sin filtros
            medicosFlux = getUnfilteredMedicosWithSort(sortBy, isAscending, size, offset);
        } else {
            // Con filtros
            medicosFlux = getFilteredMedicosWithSort(nombre, especialidadId, sortBy, isAscending, size, offset);
        }

        // Transformar y construir la respuesta
        return medicosFlux
                .map(medicoMapper::toDto)
                .collectList()
                .map(medicos -> {
                    // Determinamos si es la última página basándonos en el tamaño de la página
                    boolean isLast = medicos.size() < pageable.getPageSize();

                    return PageResponseDto.<MedicoDto>builder()
                            .content(medicos)
                            .pageNumber(pageable.getPageNumber())
                            .pageSize(pageable.getPageSize())
                            .first(pageable.getPageNumber() == 0)
                            .last(isLast)
                            .empty(medicos.isEmpty())
                            .build();
                })
                .doOnSuccess(page -> log.info("Consulta paginada completada: {} resultados", page.getContent().size()))
                .onErrorResume(error -> {
                    log.error("Error al obtener médicos paginados: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    private Flux<Medico> getUnfilteredMedicosWithSort(String sortBy, boolean isAscending, int size, long offset) {
        // Validar que los parámetros sean correctos
        if (size < 0 || offset < 0) {
            return Flux.error(new IllegalArgumentException("Tamaño y offset deben ser mayores o iguales a cero"));
        }

        // Usamos solo los métodos que existen en el repositorio
        if ("id".equals(sortBy)) {
            return isAscending
                    ? medicoRepository.findAllPagedOrderByIdAsc(size, offset)
                    : medicoRepository.findAllPagedOrderByIdDesc(size, offset);
        } else {
            // Para otros casos, usamos el método por defecto y ordenamos en memoria
            return medicoRepository.findAllPaged(size, offset);
        }
    }

    private Flux<Medico> getFilteredMedicosWithSort(String nombre, Long especialidadId, String sortBy, boolean isAscending, int size, long offset) {
        // Validar que los parámetros sean correctos
        if (size < 0 || offset < 0) {
            return Flux.error(new IllegalArgumentException("Tamaño y offset deben ser mayores o iguales a cero"));
        }

        // Usamos solo los métodos que existen en el repositorio
        if ("id".equals(sortBy)) {
            return isAscending
                    ? medicoRepository.findByNombreAndEspecialidadIdOrderByIdAsc(nombre, especialidadId, size, offset)
                    : medicoRepository.findByNombreAndEspecialidadIdOrderByIdDesc(nombre, especialidadId, size, offset);
        } else {
            // Para otros casos, usamos el método por defecto
            return medicoRepository.findByNombreAndEspecialidadIdOrderByIdAsc(nombre, especialidadId, size, offset);
        }
    }

    private String getSortProperty(Pageable pageable) {
        if (pageable == null || pageable.getSort().isEmpty()) {
            return "id"; // Valor por defecto
        }
        return pageable.getSort().iterator().next().getProperty();
    }

    private boolean isAscendingSort(Pageable pageable) {
        if (pageable == null || pageable.getSort().isEmpty()) {
            return true; // Valor por defecto
        }
        return pageable.getSort().iterator().next().getDirection().isAscending();
    }

    @Override
    public void validateSortParameters(String sortBy, String sortOrder) {
        // Validar el campo de ordenamiento
        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Campo de ordenamiento no válido: " + sortBy +
                    ". Campos válidos: " + String.join(", ", VALID_SORT_FIELDS));
        }

        // Validar la dirección de ordenamiento
        if (!sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) &&
                !sortOrder.equalsIgnoreCase(Sort.Direction.DESC.name())) {
            throw new IllegalArgumentException("Dirección de ordenamiento no válida: " + sortOrder +
                    ". Direcciones válidas: ASC, DESC");
        }
    }

    /**
     * Valida que los datos del DTO del médico sean correctos
     * @param medicoDto El DTO del médico a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validateMedicoDto(MedicoDto medicoDto) {
        // Verificar que el DTO no sea nulo
        if (medicoDto == null) {
            log.error("Error de validación: El DTO del médico es nulo");
            return false;
        }
        
        // Validar el nombre
        if (medicoDto.getNombre() == null || medicoDto.getNombre().trim().isEmpty()) {
            log.error("Nombre de médico inválido: {}", medicoDto.getNombre());
            return false;
        }

        // Validar el ID de especialidad
        if (medicoDto.getEspecialidadId() == null) {
            log.error("Especialidad ID de médico inválida: null");
            return false;
        }

        return true;
    }
}