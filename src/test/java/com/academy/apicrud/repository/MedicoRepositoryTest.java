package com.academy.apicrud.repository;

import com.academy.apicrud.model.domain.Medico;
import com.academy.apicrud.model.response.ResponseMedico;
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
@Feature("Medico Repository")
public class MedicoRepositoryTest {

    @Mock
    private MedicoRepository medicoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Medico medico;
    private ResponseMedico responseMedico;

    @BeforeEach
    public void setup() throws IOException {
        // Cargar datos de prueba desde JSON
        medico = objectMapper.readValue(
                new ClassPathResource("mock/medico.json").getInputStream(), 
                Medico.class
        );
        
        // Crear respuesta con especialidad
        responseMedico = new ResponseMedico();
        responseMedico.setId(medico.getId());
        responseMedico.setNombreMedico(medico.getNombre());
        responseMedico.setEspecialidadId(medico.getEspecialidadId());
        responseMedico.setNombreEspecialidad("Cardiología");
    }

    @Test
    @DisplayName("Guardar médico")
    @Story("Guardar médico en la base de datos")
    @Description("Debe guardar un médico correctamente en la base de datos")
    public void saveMedico_Success() {
        // Arrange (Given)
        Medico medicoToSave = new Medico();
        medicoToSave.setNombre(medico.getNombre());
        medicoToSave.setEspecialidadId(medico.getEspecialidadId());
        
        Medico savedMedico = new Medico();
        savedMedico.setId(1L);
        savedMedico.setNombre(medico.getNombre());
        savedMedico.setEspecialidadId(medico.getEspecialidadId());
        
        Mockito.when(medicoRepository.save(Mockito.any(Medico.class)))
                .thenReturn(Mono.just(savedMedico));

        // Act (When)
        Mono<Medico> result = medicoRepository.save(medicoToSave);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(saved -> {
                    return saved.getId() != null && 
                           medico.getNombre().equals(saved.getNombre()) &&
                           saved.getEspecialidadId().equals(medico.getEspecialidadId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Buscar médico por ID")
    @Story("Buscar médico por ID")
    @Description("Debe encontrar un médico por su ID")
    public void findMedicoById_Success() {
        // Arrange (Given)
        Long id = 1L;
        Mockito.when(medicoRepository.findById(id))
                .thenReturn(Mono.just(medico));

        // Act (When)
        Mono<Medico> result = medicoRepository.findById(id);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(found -> {
                    return found.getId() != null && 
                           medico.getNombre().equals(found.getNombre()) &&
                           found.getEspecialidadId().equals(medico.getEspecialidadId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Buscar médicos con especialidad")
    @Story("Buscar médicos con su especialidad")
    @Description("Debe encontrar todos los médicos con información de su especialidad")
    public void findAllMedicoWithEspecialidad_Success() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findAllMedicoWithEspecialidad())
                .thenReturn(Flux.just(responseMedico));

        // Act (When)
        Flux<ResponseMedico> result = medicoRepository.findAllMedicoWithEspecialidad();

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    return response.getId() != null && 
                           response.getNombreMedico() != null &&
                           response.getEspecialidadId() != null &&
                           response.getNombreEspecialidad() != null;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Buscar médico con especialidad por ID")
    @Story("Buscar médico con su especialidad por ID")
    @Description("Debe encontrar un médico con información de su especialidad por ID")
    public void findMedicoWithEspecialidadById_Success() {
        // Arrange (Given)
        Long id = 1L;
        Mockito.when(medicoRepository.findMedicoWithEspecialidadById(id))
                .thenReturn(Mono.just(responseMedico));

        // Act (When)
        Mono<ResponseMedico> result = medicoRepository.findMedicoWithEspecialidadById(id);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    return response.getId() != null && 
                           response.getNombreMedico() != null &&
                           response.getEspecialidadId() != null &&
                           response.getNombreEspecialidad() != null;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Buscar médicos paginados")
    @Story("Buscar médicos paginados")
    @Description("Debe encontrar médicos con paginación")
    public void findAllPaged_Success() {
        // Arrange (Given)
        int pageSize = 3;
        int pageNumber = 0;
        Mockito.when(medicoRepository.findAllPaged(pageSize, pageNumber))
                .thenReturn(Flux.just(medico, medico, medico));

        // Act (When)
        Flux<Medico> result = medicoRepository.findAllPaged(pageSize, pageNumber);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    @DisplayName("Buscar médicos filtrados por nombre y especialidad")
    @Story("Buscar médicos filtrados")
    @Description("Debe encontrar médicos filtrados por nombre y especialidad")
    public void findByNombreAndEspecialidadId_Success() {
        // Arrange (Given)
        String nombre = "Dr";
        Long especialidadId = 1L;
        int pageSize = 10;
        int pageNumber = 0;
        
        Mockito.when(medicoRepository.findByNombreAndEspecialidadIdOrderByIdAsc(nombre, especialidadId, pageSize, pageNumber))
                .thenReturn(Flux.just(medico));

        // Act (When)
        Flux<Medico> result = medicoRepository.findByNombreAndEspecialidadIdOrderByIdAsc(nombre, especialidadId, pageSize, pageNumber);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(medicoResult -> 
                    medicoResult.getId() != null && 
                    medico.getNombre().equals(medicoResult.getNombre()) &&
                    medicoResult.getEspecialidadId().equals(medico.getEspecialidadId())
                )
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Buscar médicos ordenados por ID ascendente")
    @Story("Buscar médicos ordenados")
    @Description("Debe encontrar médicos ordenados por ID ascendente")
    public void findAllPagedOrderByIdAsc_Success() {
        // Arrange (Given)
        int pageSize = 10;
        int pageNumber = 0;
        
        Mockito.when(medicoRepository.findAllPagedOrderByIdAsc(pageSize, pageNumber))
                .thenReturn(Flux.just(medico));

        // Act (When)
        Flux<Medico> result = medicoRepository.findAllPagedOrderByIdAsc(pageSize, pageNumber);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(medicoResult -> 
                    medicoResult.getId() != null && 
                    medico.getNombre().equals(medicoResult.getNombre()) &&
                    medicoResult.getEspecialidadId().equals(medico.getEspecialidadId())
                )
                .verifyComplete();
    }
}
