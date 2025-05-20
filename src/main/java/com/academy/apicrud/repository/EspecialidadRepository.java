package com.academy.apicrud.repository;

import com.academy.apicrud.model.domain.Especialidad;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends ReactiveCrudRepository<Especialidad, Long> {
    // Los métodos básicos de CRUD ya son proporcionados por ReactiveCrudRepository
    // Se pueden añadir consultas personalizadas si es necesario
}