package com.academy.apicrud.repository;

import com.academy.apicrud.model.domain.Medico;
import com.academy.apicrud.model.response.ResponseMedico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MedicoRepository extends ReactiveCrudRepository<Medico, Long> {

    @Query("SELECT m.id AS id, m.nombre AS nombreMedico, " +
            "m.especialidad_id AS especialidadId, e.nombre AS nombreEspecialidad " +
            "FROM medico m " +
            "INNER JOIN especialidad e ON m.especialidad_id = e.id")
    Flux<ResponseMedico> findAllMedicoWithEspecialidad();

    @Query("SELECT m.id AS id, m.nombre AS nombreMedico, " +
            "m.especialidad_id AS especialidadId, e.nombre AS nombreEspecialidad " +
            "FROM medico m " +
            "INNER JOIN especialidad e ON m.especialidad_id = e.id " +
            "WHERE m.id = :id")
    Mono<ResponseMedico> findMedicoWithEspecialidadById(Long id);

    // Consulta paginada general
    @Query("SELECT * FROM medico LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPaged(int size, long offset);

    // Consulta paginada con orden
    @Query("SELECT * FROM medico ORDER BY id ASC LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPagedOrderByIdAsc(int size, long offset);

    @Query("SELECT * FROM medico ORDER BY id DESC LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPagedOrderByIdDesc(int size, long offset);

    @Query("SELECT * FROM medico ORDER BY nombre ASC LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPagedOrderByNombreAsc(int size, long offset);

    @Query("SELECT * FROM medico ORDER BY nombre DESC LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPagedOrderByNombreDesc(int size, long offset);

    @Query("SELECT * FROM medico ORDER BY especialidad_id ASC LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPagedOrderByEspecialidadIdAsc(int size, long offset);

    @Query("SELECT * FROM medico ORDER BY especialidad_id DESC LIMIT :size OFFSET :offset")
    Flux<Medico> findAllPagedOrderByEspecialidadIdDesc(int size, long offset);

    // Consultas paginadas filtradas
    @Query("SELECT * FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId) " +
            "ORDER BY id ASC LIMIT :size OFFSET :offset")
    Flux<Medico> findByNombreAndEspecialidadIdOrderByIdAsc(String nombre, Long especialidadId, int size, long offset);

    @Query("SELECT * FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId) " +
            "ORDER BY id DESC LIMIT :size OFFSET :offset")
    Flux<Medico> findByNombreAndEspecialidadIdOrderByIdDesc(String nombre, Long especialidadId, int size, long offset);

    @Query("SELECT * FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId) " +
            "ORDER BY nombre ASC LIMIT :size OFFSET :offset")
    Flux<Medico> findByNombreAndEspecialidadIdOrderByNombreAsc(String nombre, Long especialidadId, int size, long offset);

    @Query("SELECT * FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId) " +
            "ORDER BY nombre DESC LIMIT :size OFFSET :offset")
    Flux<Medico> findByNombreAndEspecialidadIdOrderByNombreDesc(String nombre, Long especialidadId, int size, long offset);

    @Query("SELECT * FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId) " +
            "ORDER BY especialidad_id ASC LIMIT :size OFFSET :offset")
    Flux<Medico> findByNombreAndEspecialidadIdOrderByEspecialidadIdAsc(String nombre, Long especialidadId, int size, long offset);

    @Query("SELECT * FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId) " +
            "ORDER BY especialidad_id DESC LIMIT :size OFFSET :offset")
    Flux<Medico> findByNombreAndEspecialidadIdOrderByEspecialidadIdDesc(String nombre, Long especialidadId, int size, long offset);

    // Consultas para contar
    @Query("SELECT COUNT(*) FROM medico")
    Mono<Long> count();

    @Query("SELECT COUNT(*) FROM medico WHERE (:nombre IS NULL OR nombre LIKE CONCAT('%', :nombre, '%')) " +
            "AND (:especialidadId IS NULL OR especialidad_id = :especialidadId)")
    Mono<Long> countByNombreAndEspecialidadId(String nombre, Long especialidadId);
}