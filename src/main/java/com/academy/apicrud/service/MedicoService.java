package com.academy.apicrud.service;

import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.dto.PageResponseDto;
import com.academy.apicrud.model.response.ResponseMedico;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MedicoService {

    /**
     * Obtiene todos los médicos con información de su especialidad
     * @return Flux de objetos ResponseMedico
     */
    Flux<ResponseMedico> getAllMedicosWithEspecialidad();

    /**
     * Obtiene un médico por su ID con información de su especialidad
     * @param id ID del médico
     * @return Mono con el ResponseMedico o empty si no existe
     */
    Mono<ResponseMedico> getMedicoWithEspecialidadById(Long id);

    /**
     * Obtiene todos los médicos
     * @return Flux de objetos MedicoDto
     */
    Flux<MedicoDto> getAllMedicos();

    /**
     * Obtiene un médico por su ID
     * @param id ID del médico
     * @return Mono con el MedicoDto o empty si no existe
     */
    Mono<MedicoDto> getMedicoById(Long id);

    /**
     * Guarda un nuevo médico
     * @param medicoDto Objeto MedicoDto a guardar
     * @return Mono con el MedicoDto guardado
     */
    Mono<MedicoDto> saveMedico(MedicoDto medicoDto);

    /**
     * Actualiza un médico existente
     * @param id ID del médico a actualizar
     * @param medicoDto Objeto MedicoDto con los datos actualizados
     * @return Mono con el MedicoDto actualizado o empty si no existe
     */
    Mono<MedicoDto> updateMedico(Long id, MedicoDto medicoDto);

    /**
     * Elimina un médico por su ID
     * @param id ID del médico a eliminar
     * @return Mono<Void> que completa cuando se elimina el médico
     */
    Mono<Void> deleteMedico(Long id);

    /**
     * Obtiene médicos paginados y filtrados por nombre y especialidad
     * @param nombre Filtro por nombre (opcional)
     * @param especialidadId Filtro por ID de especialidad (opcional)
     * @param pageable Información de paginación
     * @return Mono con la información de página y los médicos
     */
    Mono<PageResponseDto<MedicoDto>> getMedicosPaginados(String nombre, Long especialidadId, Pageable pageable);

    /**
     * Valida parámetros de ordenamiento
     * @param sortBy Campo de ordenamiento
     * @param sortOrder Dirección de ordenamiento (asc/desc)
     * @throws IllegalArgumentException si los parámetros no son válidos
     */
    void validateSortParameters(String sortBy, String sortOrder);
}