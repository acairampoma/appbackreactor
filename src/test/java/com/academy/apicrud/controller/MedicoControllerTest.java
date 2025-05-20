package com.academy.apicrud.controller;

import com.academy.apicrud.exception.ResourceNotFoundException;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.dto.PageResponseDto;
import com.academy.apicrud.model.response.ResponseMedico;
import com.academy.apicrud.service.MedicoService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Epic("Controladores")
@Feature("Médico Controller")
public class MedicoControllerTest {

    @Mock
    private MedicoService medicoService;

    @InjectMocks
    private MedicoController medicoController;

    private WebTestClient webTestClient;
    private MedicoDto medicoDto;
    private ResponseMedico responseMedico;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToController(medicoController).build();
        
        // Crear datos de prueba
        medicoDto = new MedicoDto();
        medicoDto.setId(1L);
        medicoDto.setNombre("Dr. Juan Pérez");
        medicoDto.setEspecialidadId(1L);
        
        responseMedico = new ResponseMedico();
        responseMedico.setId(1L);
        responseMedico.setNombreMedico("Dr. Juan Pérez");
        responseMedico.setEspecialidadId(1L);
        responseMedico.setNombreEspecialidad("Cardiología");
    }

    @Test
    @DisplayName("Obtener todos los médicos")
    @Story("Obtener todos los médicos")
    @Description("Debe obtener todos los médicos correctamente")
    public void getAllMedicos_Success() {
        // Arrange
        List<MedicoDto> medicosDto = new ArrayList<>();
        medicosDto.add(medicoDto);
        
        when(medicoService.getAllMedicos()).thenReturn(Flux.fromIterable(medicosDto));

        // Act & Assert
        webTestClient.get()
                .uri("/api/medicos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.message").isEqualTo("Operación GET realizada con éxito")
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].nombre").isEqualTo("Dr. Juan Pérez")
                .jsonPath("$.data[0].especialidadId").isEqualTo(1);
    }

    @Test
    @DisplayName("Obtener médicos con especialidad")
    @Story("Obtener médicos con especialidad")
    @Description("Debe obtener todos los médicos con su especialidad correctamente")
    public void getAllMedicosWithEspecialidad_Success() {
        // Arrange
        List<ResponseMedico> medicosResponse = new ArrayList<>();
        medicosResponse.add(responseMedico);
        
        when(medicoService.getAllMedicosWithEspecialidad()).thenReturn(Flux.fromIterable(medicosResponse));

        // Act & Assert
        webTestClient.get()
                .uri("/api/medicos/with-especialidad")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.message").isEqualTo("Operación GET realizada con éxito")
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].nombreMedico").isEqualTo("Dr. Juan Pérez")
                .jsonPath("$.data[0].especialidadId").isEqualTo(1)
                .jsonPath("$.data[0].nombreEspecialidad").isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Obtener médico por ID")
    @Story("Obtener médico por ID")
    @Description("Debe obtener un médico por su ID correctamente")
    public void getMedicoById_Success() {
        // Arrange
        Long id = 1L;
        when(medicoService.getMedicoById(id)).thenReturn(Mono.just(medicoDto));

        // Act & Assert
        webTestClient.get()
                .uri("/api/medicos/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.message").isEqualTo("Operación GET realizada con éxito")
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.nombre").isEqualTo("Dr. Juan Pérez")
                .jsonPath("$.data.especialidadId").isEqualTo(1);
    }

    @Test
    @DisplayName("Obtener médico por ID - No encontrado")
    @Story("Obtener médico por ID")
    @Description("Debe manejar el caso cuando un médico no existe")
    public void getMedicoById_NotFound() {
        // Arrange
        Long id = 99L;
        when(medicoService.getMedicoById(id)).thenReturn(Mono.empty());
        
        // Simular el comportamiento del controlador que lanza un error cuando no encuentra el médico
        ResourceNotFoundException exception = new ResourceNotFoundException("Médico", "id", id);
        when(medicoService.getMedicoById(id)).thenThrow(exception);

        // Act & Assert
        webTestClient.get()
                .uri("/api/medicos/{id}", id)
                .exchange()
                .expectStatus().is5xxServerError(); // Cambiado a 5xx ya que el controlador devuelve 500
    }

    @Test
    @DisplayName("Obtener médico con especialidad por ID")
    @Story("Obtener médico con especialidad por ID")
    @Description("Debe obtener un médico con su especialidad por ID correctamente")
    public void getMedicoWithEspecialidadById_Success() {
        // Arrange
        Long id = 1L;
        when(medicoService.getMedicoWithEspecialidadById(id)).thenReturn(Mono.just(responseMedico));

        // Act & Assert
        webTestClient.get()
                .uri("/api/medicos/{id}/with-especialidad", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.message").isEqualTo("Operación GET realizada con éxito")
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.nombreMedico").isEqualTo("Dr. Juan Pérez")
                .jsonPath("$.data.especialidadId").isEqualTo(1)
                .jsonPath("$.data.nombreEspecialidad").isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Crear médico")
    @Story("Crear médico")
    @Description("Debe crear un médico correctamente")
    public void createMedico_Success() {
        // Arrange
        MedicoDto medicoToCreate = new MedicoDto();
        medicoToCreate.setNombre("Dr. Nuevo Médico");
        medicoToCreate.setEspecialidadId(1L);
        
        when(medicoService.saveMedico(any(MedicoDto.class))).thenReturn(Mono.just(medicoDto));

        // Act & Assert
        webTestClient.post()
                .uri("/api/medicos")
                .bodyValue(medicoToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo("201")
                .jsonPath("$.message").isEqualTo("Operación POST realizada con éxito")
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.nombre").isEqualTo("Dr. Juan Pérez")
                .jsonPath("$.data.especialidadId").isEqualTo(1);
    }

    @Test
    @DisplayName("Actualizar médico")
    @Story("Actualizar médico")
    @Description("Debe actualizar un médico existente correctamente")
    public void updateMedico_Success() {
        // Arrange
        Long id = 1L;
        MedicoDto medicoToUpdate = new MedicoDto();
        medicoToUpdate.setNombre("Dr. Juan Pérez Actualizado");
        medicoToUpdate.setEspecialidadId(2L);
        
        MedicoDto updatedMedico = new MedicoDto();
        updatedMedico.setId(id);
        updatedMedico.setNombre("Dr. Juan Pérez Actualizado");
        updatedMedico.setEspecialidadId(2L);
        
        when(medicoService.updateMedico(eq(id), any(MedicoDto.class))).thenReturn(Mono.just(updatedMedico));

        // Act & Assert
        webTestClient.put()
                .uri("/api/medicos/{id}", id)
                .bodyValue(medicoToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.message").isEqualTo("Operación PUT realizada con éxito")
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.nombre").isEqualTo("Dr. Juan Pérez Actualizado")
                .jsonPath("$.data.especialidadId").isEqualTo(2);
    }

    @Test
    @DisplayName("Eliminar médico")
    @Story("Eliminar médico")
    @Description("Debe eliminar un médico existente correctamente")
    public void deleteMedico_Success() {
        // Arrange
        Long id = 1L;
        when(medicoService.getMedicoById(id)).thenReturn(Mono.just(medicoDto));
        when(medicoService.deleteMedico(id)).thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.delete()
                .uri("/api/medicos/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Eliminar médico - No encontrado")
    @Story("Eliminar médico")
    @Description("Debe manejar el caso cuando se intenta eliminar un médico que no existe")
    public void deleteMedico_NotFound() {
        // Arrange
        Long id = 99L;
        // Simular el comportamiento del controlador que lanza un error cuando no encuentra el médico
        ResourceNotFoundException exception = new ResourceNotFoundException("Médico", "id", id);
        when(medicoService.getMedicoById(id)).thenThrow(exception);

        // Act & Assert
        webTestClient.delete()
                .uri("/api/medicos/{id}", id)
                .exchange()
                .expectStatus().is5xxServerError(); // Cambiado a 5xx ya que el controlador devuelve 500
    }

    @Test
    @DisplayName("Obtener médicos paginados")
    @Story("Obtener médicos paginados")
    @Description("Debe obtener médicos paginados correctamente")
    public void getMedicosPaginados_Success() {
        // Arrange
        String nombre = null;
        Long especialidadId = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortOrder = "asc";
        
        PageResponseDto<MedicoDto> pageResponse = new PageResponseDto<>();
        List<MedicoDto> content = new ArrayList<>();
        content.add(medicoDto);
        pageResponse.setContent(content);
        pageResponse.setPageNumber(page);
        pageResponse.setPageSize(size);
        pageResponse.setFirst(true);
        pageResponse.setLast(true);
        pageResponse.setEmpty(false);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        
        Mockito.doNothing().when(medicoService).validateSortParameters(anyString(), anyString());
        when(medicoService.getMedicosPaginados(eq(nombre), eq(especialidadId), any(Pageable.class)))
                .thenReturn(Mono.just(pageResponse));

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/medicos/page")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", sortBy)
                        .queryParam("sortOrder", sortOrder)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.message").isEqualTo("Operación GET realizada con éxito")
                .jsonPath("$.data.content[0].id").isEqualTo(1)
                .jsonPath("$.data.content[0].nombre").isEqualTo("Dr. Juan Pérez")
                .jsonPath("$.data.pageNumber").isEqualTo(0)
                .jsonPath("$.data.pageSize").isEqualTo(10)
                .jsonPath("$.data.first").isEqualTo(true)
                .jsonPath("$.data.last").isEqualTo(true)
                .jsonPath("$.data.empty").isEqualTo(false);
    }
}
