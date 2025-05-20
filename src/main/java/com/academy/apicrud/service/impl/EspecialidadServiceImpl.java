package com.academy.apicrud.service.impl;

import com.academy.apicrud.exception.ResourceNotFoundException;
import com.academy.apicrud.model.domain.Especialidad;
import com.academy.apicrud.repository.EspecialidadRepository;
import com.academy.apicrud.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    @Override
    public Flux<Especialidad> getAllEspecialidades() {
        log.info("Obteniendo todas las especialidades");
        return especialidadRepository.findAll();
    }

    @Override
    public Mono<Especialidad> getEspecialidadById(Long id) {
        log.info("Buscando especialidad con ID: {}", id);
        return especialidadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Especialidad", "id", id)));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        log.info("Verificando si existe especialidad con ID: {}", id);
        return especialidadRepository.existsById(id);
    }

    @Override
    public Mono<Especialidad> saveEspecialidad(Especialidad especialidad) {
        log.info("Guardando nueva especialidad: {}", especialidad);
        return validarEspecialidad(especialidad)
                .flatMap(especialidadRepository::save);
    }

    @Override
    public Mono<Especialidad> updateEspecialidad(Long id, Especialidad especialidad) {
        log.info("Actualizando especialidad con ID: {}", id);
        return especialidadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Especialidad", "id", id)))
                .flatMap(existingEspecialidad -> validarEspecialidad(especialidad)
                        .map(validEspecialidad -> {
                            existingEspecialidad.setNombre(validEspecialidad.getNombre());
                            return existingEspecialidad;
                        }))
                .flatMap(especialidadRepository::save);
    }

    @Override
    public Mono<Void> deleteEspecialidad(Long id) {
        log.info("Eliminando especialidad con ID: {}", id);
        return especialidadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Especialidad", "id", id)))
                .flatMap(especialidadRepository::delete);
    }

    private Mono<Especialidad> validarEspecialidad(Especialidad especialidad) {
        if (especialidad.getNombre() == null || especialidad.getNombre().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la especialidad no puede ser nulo o vacío"));
        }
        return Mono.just(especialidad);
    }
}
