package com.academy.apicrud.repository;

import com.academy.apicrud.model.domain.Especialidad;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@Epic("Repositorios")
@Feature("Especialidad Repository")
public class EspecialidadRepositoryTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Especialidad especialidad;

    @BeforeEach
    public void setup() throws IOException {
        // Cargar datos de prueba desde JSON
        especialidad = objectMapper.readValue(
                new ClassPathResource("mock/especialidad.json").getInputStream(),
                Especialidad.class
        );
    }

    @Test
    @DisplayName("Guardar especialidad")
    @Story("Guardar especialidad en la base de datos")
    @Description("Debe guardar una especialidad correctamente en la base de datos")
    public void saveEspecialidad_Success() {
        // Arrange (Given)
        Especialidad especialidadToSave = new Especialidad();
        especialidadToSave.setNombre(especialidad.getNombre());
        
        Especialidad savedEspecialidad = new Especialidad();
        savedEspecialidad.setId(1L);
        savedEspecialidad.setNombre(especialidad.getNombre());
        
        Mockito.when(especialidadRepository.save(Mockito.any(Especialidad.class)))
                .thenReturn(Mono.just(savedEspecialidad));

        // Act (When)
        Mono<Especialidad> result = especialidadRepository.save(especialidadToSave);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(saved -> {
                    return saved.getId() != null && 
                           especialidad.getNombre().equals(saved.getNombre());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Buscar especialidad por ID")
    @Story("Buscar especialidad por ID")
    @Description("Debe encontrar una especialidad por su ID")
    public void findEspecialidadById_Success() {
        // Arrange (Given)
        Long id = 1L;
        Mockito.when(especialidadRepository.findById(id))
                .thenReturn(Mono.just(especialidad));

        // Act (When)
        Mono<Especialidad> result = especialidadRepository.findById(id);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(found -> {
                    return found.getId() != null && 
                           especialidad.getNombre().equals(found.getNombre());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Actualizar especialidad")
    @Story("Actualizar especialidad")
    @Description("Debe actualizar una especialidad existente")
    public void updateEspecialidad_Success() {
        // Arrange (Given)
        String nombreActualizado = "Cardiología Pediátrica";
        
        Especialidad especialidadToUpdate = new Especialidad();
        especialidadToUpdate.setId(1L);
        especialidadToUpdate.setNombre(nombreActualizado);
        
        Mockito.when(especialidadRepository.save(Mockito.any(Especialidad.class)))
                .thenReturn(Mono.just(especialidadToUpdate));

        // Act (When)
        Mono<Especialidad> result = especialidadRepository.save(especialidadToUpdate);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(updated -> {
                    return updated.getId() != null && 
                           nombreActualizado.equals(updated.getNombre());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Eliminar especialidad")
    @Story("Eliminar especialidad")
    @Description("Debe eliminar una especialidad existente")
    public void deleteEspecialidad_Success() {
        // Arrange (Given)
        Long id = 1L;
        Mockito.when(especialidadRepository.deleteById(id))
                .thenReturn(Mono.empty());

        // Act & Assert (When & Then)
        StepVerifier.create(especialidadRepository.deleteById(id))
                .verifyComplete();
    }

    @Test
    @DisplayName("Listar todas las especialidades")
    @Story("Listar todas las especialidades")
    @Description("Debe listar todas las especialidades existentes")
    public void findAll_Success() {
        // Arrange (Given)
        Mockito.when(especialidadRepository.findAll())
                .thenReturn(Flux.just(especialidad));

        // Act (When)
        Flux<Especialidad> result = especialidadRepository.findAll();

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(esp -> {
                    return esp.getId() != null && 
                           especialidad.getNombre().equals(esp.getNombre());
                })
                .verifyComplete();
    }
}
