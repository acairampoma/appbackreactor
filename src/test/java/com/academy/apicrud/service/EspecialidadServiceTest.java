package com.academy.apicrud.service;

import com.academy.apicrud.model.domain.Especialidad;
import com.academy.apicrud.repository.EspecialidadRepository;
import com.academy.apicrud.service.impl.EspecialidadServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@Epic("Servicios")
@Feature("Especialidad Service")
public class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private EspecialidadServiceImpl especialidadService;

    private Especialidad especialidad;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() throws IOException {
        // Cargar datos de prueba desde JSON
        especialidad = objectMapper.readValue(
                new ClassPathResource("mock/especialidad.json").getInputStream(),
                Especialidad.class
        );
    }

    @Test
    @DisplayName("Obtener todas las especialidades")
    @Story("Obtener todas las especialidades")
    @Description("Debe obtener todas las especialidades correctamente")
    public void getAllEspecialidades_Success() {
        // Arrange (Given)
        Mockito.when(especialidadRepository.findAll())
                .thenReturn(Flux.just(especialidad));

        // Act (When)
        Flux<Especialidad> result = especialidadService.getAllEspecialidades();

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(esp -> esp.getId().equals(especialidad.getId()) && 
                                   esp.getNombre().equals(especialidad.getNombre()))
                .verifyComplete();
    }



    @Test
    @DisplayName("Obtener especialidad por ID")
    @Story("Obtener especialidad por ID")
    @Description("Debe obtener una especialidad por su ID correctamente")
    public void getEspecialidadById_Success() {
        // Arrange (Given)
        Mockito.when(especialidadRepository.findById(especialidad.getId()))
                .thenReturn(Mono.just(especialidad));

        // Act (When)
        Mono<Especialidad> result = especialidadService.getEspecialidadById(especialidad.getId());

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(esp -> esp.getId().equals(especialidad.getId()) && 
                                   esp.getNombre().equals(especialidad.getNombre()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener especialidad por ID - No encontrada")
    @Story("Obtener especialidad por ID")
    @Description("Debe lanzar ResourceNotFoundException cuando no se encuentra la especialidad")
    public void getEspecialidadById_NotFound() {
        // Arrange (Given)
        Long idNoExistente = 99L;
        Mockito.when(especialidadRepository.findById(idNoExistente))
                .thenReturn(Mono.empty());

        // Act (When)
        Mono<Especialidad> result = especialidadService.getEspecialidadById(idNoExistente);

        // Assert (Then)
        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("Guardar especialidad")
    @Story("Guardar especialidad")
    @Description("Debe guardar una especialidad correctamente")
    public void saveEspecialidad_Success() {
        // Arrange (Given)
        Especialidad nuevaEspecialidad = new Especialidad();
        nuevaEspecialidad.setNombre("Nueva " + especialidad.getNombre());

        Especialidad especialidadGuardada = new Especialidad();
        especialidadGuardada.setId(3L);
        especialidadGuardada.setNombre(nuevaEspecialidad.getNombre());

        Mockito.when(especialidadRepository.save(Mockito.any(Especialidad.class)))
                .thenReturn(Mono.just(especialidadGuardada));

        // Act (When)
        Mono<Especialidad> result = especialidadService.saveEspecialidad(nuevaEspecialidad);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(esp -> esp.getId() == 3L && 
                                   esp.getNombre().equals(nuevaEspecialidad.getNombre()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Actualizar especialidad")
    @Story("Actualizar especialidad")
    @Description("Debe actualizar una especialidad existente correctamente")
    public void updateEspecialidad_Success() {
        // Arrange (Given)
        Especialidad especialidadActualizada = new Especialidad();
        especialidadActualizada.setNombre(especialidad.getNombre() + " Actualizada");

        Especialidad especialidadGuardada = new Especialidad();
        especialidadGuardada.setId(especialidad.getId());
        especialidadGuardada.setNombre(especialidadActualizada.getNombre());

        Mockito.when(especialidadRepository.findById(especialidad.getId()))
                .thenReturn(Mono.just(especialidad));

        Mockito.when(especialidadRepository.save(Mockito.any(Especialidad.class)))
                .thenReturn(Mono.just(especialidadGuardada));

        // Act (When)
        Mono<Especialidad> result = especialidadService.updateEspecialidad(especialidad.getId(), especialidadActualizada);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(esp -> esp.getId().equals(especialidad.getId()) && 
                                   esp.getNombre().equals(especialidadActualizada.getNombre()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Actualizar especialidad - No encontrada")
    @Story("Actualizar especialidad")
    @Description("Debe lanzar ResourceNotFoundException cuando no se encuentra la especialidad a actualizar")
    public void updateEspecialidad_NotFound() {
        // Arrange (Given)
        Long idNoExistente = 99L;
        Especialidad especialidadActualizada = new Especialidad();
        especialidadActualizada.setNombre(especialidad.getNombre() + " Actualizada");

        Mockito.when(especialidadRepository.findById(idNoExistente))
                .thenReturn(Mono.empty());

        // Act (When)
        Mono<Especialidad> result = especialidadService.updateEspecialidad(idNoExistente, especialidadActualizada);

        // Assert (Then)
        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("Eliminar especialidad")
    @Story("Eliminar especialidad")
    @Description("Debe eliminar una especialidad existente correctamente")
    public void deleteEspecialidad_Success() {
        // Arrange (Given)
        Mockito.when(especialidadRepository.findById(especialidad.getId()))
                .thenReturn(Mono.just(especialidad));

        Mockito.when(especialidadRepository.delete(Mockito.any(Especialidad.class)))
                .thenReturn(Mono.empty());

        // Act (When)
        Mono<Void> result = especialidadService.deleteEspecialidad(especialidad.getId());

        // Assert (Then)
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Verificar si existe una especialidad por ID")
    @Story("Verificar existencia de especialidad")
    @Description("Debe verificar correctamente si existe una especialidad por su ID")
    public void existsById_Success() {
        // Arrange (Given)
        Mockito.when(especialidadRepository.existsById(especialidad.getId()))
                .thenReturn(Mono.just(true));

        // Act (When)
        Mono<Boolean> result = especialidadService.existsById(especialidad.getId());

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }
}
