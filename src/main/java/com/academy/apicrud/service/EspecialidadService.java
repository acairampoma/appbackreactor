package com.academy.apicrud.service;


import com.academy.apicrud.model.domain.Especialidad;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EspecialidadService {

    /**
     * Obtiene todas las especialidades
     * @return Flux de objetos Especialidad
     */
    Flux<Especialidad> getAllEspecialidades();

    /**
     * Obtiene una especialidad por su ID
     * @param id ID de la especialidad
     * @return Mono con la Especialidad o empty si no existe
     */
    Mono<Especialidad> getEspecialidadById(Long id);

    /**
     * Guarda una nueva especialidad
     * @param especialidad Objeto Especialidad a guardar
     * @return Mono con la Especialidad guardada
     */
    Mono<Especialidad> saveEspecialidad(Especialidad especialidad);

    /**
     * Actualiza una especialidad existente
     * @param id ID de la especialidad a actualizar
     * @param especialidad Objeto Especialidad con los datos actualizados
     * @return Mono con la Especialidad actualizada o empty si no existe
     */
    Mono<Especialidad> updateEspecialidad(Long id, Especialidad especialidad);

    /**
     * Elimina una especialidad por su ID
     * @param id ID de la especialidad a eliminar
     * @return Mono<Void> que completa cuando se elimina la especialidad
     */
    Mono<Void> deleteEspecialidad(Long id);

    /**
     * Verifica si una especialidad existe por su ID
     * @param id ID de la especialidad
     * @return Mono<Boolean> que indica si la especialidad existe
     */
    Mono<Boolean> existsById(Long id);
}
