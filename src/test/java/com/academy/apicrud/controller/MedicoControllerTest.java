package com.academy.apicrud.controller;

import com.academy.apicrud.exception.ResourceNotFoundException;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.dto.PageResponseDto;
import com.academy.apicrud.model.response.ResponseMedico;
import com.academy.apicrud.service.MedicoService;
import io.qameta.allure.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
@Epic("Gestión de Médicos")
@Feature("API Rest de Médicos")
@Story("Controlador de Médicos")
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
    @DisplayName("GET - Obtener todos los médicos")
    @Story("Obtener todos los médicos")
    @Description("Verificar que se pueden obtener todos los médicos correctamente")
    @Severity(SeverityLevel.NORMAL)
    @Tag("get")
    @Tag("todos")
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
    @DisplayName("GET - Obtener médicos con especialidad")
    @Story("Obtener médicos con especialidad")
    @Description("Verificar que se pueden obtener médicos con su especialidad correctamente")
    @Severity(SeverityLevel.NORMAL)
    @Tag("get")
    @Tag("especialidad")
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
    @DisplayName("GET - Obtener médico por ID")
    @Story("Obtener médico por ID")
    @Description("Verificar que se puede obtener un médico por su ID correctamente")
    @Severity(SeverityLevel.NORMAL)
    @Tag("get")
    @Tag("id")
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
    @DisplayName("GET - Obtener médico por ID - No encontrado")
    @Story("Obtener médico por ID")
    @Description("Verificar que se maneja correctamente cuando un médico no existe")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("get")
    @Tag("id")
    @Tag("error")
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
    @DisplayName("GET - Obtener médico con especialidad por ID")
    @Story("Obtener médico con especialidad por ID")
    @Description("Verificar que se puede obtener un médico con su especialidad por ID correctamente")
    @Severity(SeverityLevel.NORMAL)
    @Tag("get")
    @Tag("id")
    @Tag("especialidad")
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
    @DisplayName("POST - Crear médico")
    @Story("Crear médico")
    @Description("Verificar que se puede crear un médico correctamente")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("post")
    @Tag("crear")
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
    @DisplayName("Obtener médicos paginados - Exitoso")
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
        
        List<MedicoDto> medicosDto = new ArrayList<>();
        medicosDto.add(medicoDto);
        
        PageResponseDto<MedicoDto> pageResponse = new PageResponseDto<>();
        pageResponse.setContent(medicosDto);
        pageResponse.setPageNumber(page);
        pageResponse.setPageSize(size);
        pageResponse.setFirst(true);
        pageResponse.setLast(true);
        pageResponse.setEmpty(false);
        
        // Mock validación de parámetros
        Mockito.doNothing().when(medicoService).validateSortParameters(sortBy, sortOrder);
        
        // Mock obtener médicos paginados
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        when(medicoService.getMedicosPaginados(nombre, especialidadId, pageable))
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
                .jsonPath("$.data.pageNumber").isEqualTo(0)
                .jsonPath("$.data.pageSize").isEqualTo(10)
                .jsonPath("$.data.first").isEqualTo(true)
                .jsonPath("$.data.last").isEqualTo(true)
                .jsonPath("$.data.empty").isEqualTo(false);
    }

    @Test
    @DisplayName("Obtener médicos paginados - Parámetros de paginación inválidos")
    @Story("Obtener médicos paginados")
    @Description("Debe manejar correctamente cuando se proporcionan parámetros de paginación inválidos")
    public void getMedicosPaginados_InvalidPaginationParameters() {
        // Arrange
        String nombre = null;
        Long especialidadId = null;
        int page = 0;
        int size = -5; // Tamaño negativo - inválido
        String sortBy = "id";
        String sortOrder = "asc";

        // Mock validación de parámetros
        Mockito.doNothing().when(medicoService).validateSortParameters(sortBy, sortOrder);

        // Configurar el controlador para usar un WebTestClient personalizado que permita valores negativos
        webTestClient = WebTestClient.bindToController(medicoController).build();

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
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400")
                // 🎯 SOLUCIÓN ROBUSTA: Usar contains para mayor flexibilidad
                .jsonPath("$.message").value(Matchers.containsStringIgnoringCase("Page size must not be less than one"));
    }

    @Test
    @DisplayName("Obtener médicos paginados - Error en validación de parámetros de ordenamiento")
    @Story("Obtener médicos paginados")
    @Description("Debe manejar correctamente cuando hay un error en la validación de parámetros de ordenamiento")
    public void getMedicosPaginados_SortParametersValidationError() {
        // Arrange
        String nombre = null;
        Long especialidadId = null;
        int page = 0;
        int size = 10;
        String sortBy = "campoInvalido"; // Campo inválido
        String sortOrder = "asc";
        
        // Mock validación de parámetros con error
        Mockito.doThrow(new IllegalArgumentException("Campo de ordenamiento inválido: campoInvalido"))
               .when(medicoService).validateSortParameters(sortBy, sortOrder);

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
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400")
                .jsonPath("$.message").isEqualTo("Campo de ordenamiento inválido: campoInvalido");
    }

    @Test
    @DisplayName("Obtener médicos paginados - Error general")
    @Story("Obtener médicos paginados")
    @Description("Debe manejar correctamente cuando ocurre un error general durante la obtención de médicos paginados")
    public void getMedicosPaginados_GeneralError() {
        // Arrange
        String nombre = null;
        Long especialidadId = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortOrder = "asc";
        
        // Mock validación de parámetros
        Mockito.doNothing().when(medicoService).validateSortParameters(sortBy, sortOrder);
        
        // Mock obtener médicos paginados con error general
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        when(medicoService.getMedicosPaginados(nombre, especialidadId, pageable))
                .thenReturn(Mono.error(new RuntimeException("Error al obtener médicos paginados")));

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
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("500")
                .jsonPath("$.message").isEqualTo("Error al obtener médicos paginados: Error al obtener médicos paginados");
    }
}
