package com.academy.apicrud.service;

import com.academy.apicrud.exception.ResourceNotFoundException;
import com.academy.apicrud.mapper.IMedicoMapper;
import com.academy.apicrud.model.domain.Medico;
import com.academy.apicrud.model.dto.MedicoDto;
import com.academy.apicrud.model.dto.PageResponseDto;
import com.academy.apicrud.model.response.ResponseMedico;
import com.academy.apicrud.repository.MedicoRepository;
import com.academy.apicrud.service.impl.MedicoServiceImpl;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
@Epic("Servicios")
@Feature("Medico Service")
public class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private IMedicoMapper medicoMapper;

    @Mock
    private EspecialidadService especialidadService;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Medico medico;
    private MedicoDto medicoDto;
    private ResponseMedico responseMedico;

    @BeforeEach
    public void setup() throws IOException {
        // Cargar datos de prueba desde JSON
        medico = objectMapper.readValue(
                new ClassPathResource("mock/medico.json").getInputStream(),
                Medico.class
        );

        // Crear DTO correspondiente
        medicoDto = new MedicoDto();
        medicoDto.setId(medico.getId());
        medicoDto.setNombre(medico.getNombre());
        medicoDto.setEspecialidadId(medico.getEspecialidadId());

        // Crear respuesta con especialidad
        responseMedico = new ResponseMedico();
        responseMedico.setId(medico.getId());
        responseMedico.setNombreMedico(medico.getNombre());
        responseMedico.setEspecialidadId(medico.getEspecialidadId());
        responseMedico.setNombreEspecialidad("Cardiología");
    }

    @Test
    @DisplayName("Obtener todos los médicos con especialidad - Caso básico")
    @Story("Obtener médicos con especialidad")
    @Description("Debe obtener todos los médicos con su especialidad correctamente")
    public void getAllMedicosWithEspecialidad_BasicSuccess() {
        // Arrange (Given)
        ResponseMedico responseMedico = new ResponseMedico();
        responseMedico.setId(1L);
        responseMedico.setNombreMedico("Dr. Juan Pérez");
        responseMedico.setEspecialidadId(1L);
        responseMedico.setNombreEspecialidad("Cardiología");

        Mockito.when(medicoRepository.findAllMedicoWithEspecialidad())
                .thenReturn(Flux.just(responseMedico));

        // Act (When)
        Flux<ResponseMedico> result = medicoService.getAllMedicosWithEspecialidad();

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(responseMedico)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener todos los médicos con especialidad - Error")
    @Story("Obtener médicos con especialidad")
    @Description("Debe manejar correctamente los errores al obtener todos los médicos con especialidad")
    public void getAllMedicosWithEspecialidad_Error() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findAllMedicoWithEspecialidad())
                .thenReturn(Flux.error(new RuntimeException("Error de base de datos")));

        // Act (When)
        Flux<ResponseMedico> result = medicoService.getAllMedicosWithEspecialidad();

        // Assert (Then)
        // El servicio maneja el error internamente y devuelve un Flux vacío
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médico con especialidad por ID - Caso básico")
    @Story("Obtener médico con especialidad")
    @Description("Debe obtener un médico con su especialidad por ID correctamente")
    public void getMedicoWithEspecialidadById_BasicSuccess() {
        // Arrange (Given)
        ResponseMedico responseMedico = new ResponseMedico();
        responseMedico.setId(1L);
        responseMedico.setNombreMedico("Dr. Juan Pérez");
        responseMedico.setEspecialidadId(1L);
        responseMedico.setNombreEspecialidad("Cardiología");

        Mockito.when(medicoRepository.findMedicoWithEspecialidadById(1L))
                .thenReturn(Mono.just(responseMedico));

        // Act (When)
        Mono<ResponseMedico> result = medicoService.getMedicoWithEspecialidadById(1L);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(responseMedico)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médico con especialidad por ID")
    @Story("Obtener médico con especialidad por ID")
    @Description("Debe obtener un médico con información de su especialidad por ID")
    public void getMedicoWithEspecialidadById_Success() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findMedicoWithEspecialidadById(1L))
                .thenReturn(Mono.just(responseMedico));

        // Act (When)
        Mono<ResponseMedico> result = medicoService.getMedicoWithEspecialidadById(1L);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(responseMedico)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médico con especialidad por ID - No encontrado")
    @Story("Obtener médico con especialidad por ID")
    @Description("Debe manejar correctamente cuando no se encuentra el médico")
    public void getMedicoWithEspecialidadById_NotFound() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findMedicoWithEspecialidadById(99L))
                .thenReturn(Mono.empty());

        // Act (When)
        Mono<ResponseMedico> result = medicoService.getMedicoWithEspecialidadById(99L);

        // Assert (Then)
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médico con especialidad por ID - Error")
    @Story("Obtener médico con especialidad por ID")
    @Description("Debe manejar correctamente los errores al buscar un médico con especialidad")
    public void getMedicoWithEspecialidadById_Error() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findMedicoWithEspecialidadById(1L))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act (When)
        Mono<ResponseMedico> result = medicoService.getMedicoWithEspecialidadById(1L);

        // Assert (Then)
        // El servicio maneja el error internamente y devuelve un Mono vacío
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener todos los médicos")
    @Story("Obtener todos los médicos")
    @Description("Debe obtener todos los médicos correctamente")
    public void getAllMedicos_Success() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findAll())
                .thenReturn(Flux.just(medico));

        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenReturn(medicoDto);

        // Act (When)
        Flux<MedicoDto> result = medicoService.getAllMedicos();

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(medicoDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener todos los médicos - Error")
    @Story("Obtener todos los médicos")
    @Description("Debe manejar correctamente los errores al obtener todos los médicos")
    public void getAllMedicos_Error() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Error de base de datos")));

        // Act (When)
        Flux<MedicoDto> result = medicoService.getAllMedicos();

        // Assert (Then)
        // El servicio maneja el error internamente y devuelve un Flux vacío
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médico por ID")
    @Story("Obtener médico por ID")
    @Description("Debe obtener un médico por su ID correctamente")
    public void getMedicoById_Success() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenReturn(medicoDto);

        // Act (When)
        Mono<MedicoDto> result = medicoService.getMedicoById(1L);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(medicoDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médico por ID - No encontrado")
    @Story("Obtener médico por ID")
    @Description("Debe manejar correctamente cuando no se encuentra un médico por ID")
    public void getMedicoById_NotFound() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(99L))
                .thenReturn(Mono.empty());

        // Act (When)
        Mono<MedicoDto> result = medicoService.getMedicoById(99L);

        // Assert (Then)
        StepVerifier.create(result)
                .verifyComplete(); // Devuelve un Mono vacío cuando no encuentra el médico
    }

    @Test
    @DisplayName("Obtener médico por ID - Error")
    @Story("Obtener médico por ID")
    @Description("Debe manejar correctamente los errores al buscar un médico por ID")
    public void getMedicoById_Error() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act (When)
        Mono<MedicoDto> result = medicoService.getMedicoById(1L);

        // Assert (Then)
        // El servicio maneja el error internamente y devuelve un Mono vacío
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Guardar médico - Caso exitoso")
    @Story("Guardar médico")
    @Description("Debe guardar un médico correctamente cuando todos los datos son válidos")
    public void saveMedico_Success() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(true));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        Mockito.when(medicoRepository.save(Mockito.any(Medico.class)))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenReturn(medicoDto);

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.saveMedico(medicoDto))
                .expectNext(medicoDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("Guardar médico - Error por médico nulo")
    @Story("Guardar médico")
    @Description("Debe manejar correctamente el error cuando se intenta guardar un médico nulo")
    public void saveMedico_NullMedico() {
        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.saveMedico(null))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("El médico no puede ser nulo"))
                .verify();
    }

    @Test
    @DisplayName("Guardar médico - Error por datos inválidos")
    @Story("Guardar médico")
    @Description("Debe manejar correctamente el error cuando los datos del médico son inválidos")
    public void saveMedico_InvalidData() {
        // Arrange (Given)
        MedicoDto invalidMedicoDto = new MedicoDto();
        // No establecemos datos requeridos para que falle la validación

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.saveMedico(invalidMedicoDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("Datos de médico inválidos"))
                .verify();
    }

    @Test
    @DisplayName("Guardar médico - Error por especialidad inexistente")
    @Story("Guardar médico")
    @Description("Debe manejar correctamente el error cuando la especialidad no existe")
    public void saveMedico_SpecialtyNotFound() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(false));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.saveMedico(medicoDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ResourceNotFoundException && 
                    throwable.getMessage().contains("Especialidad"))
                .verify();
    }

    @Test
    @DisplayName("Guardar médico - Error durante el guardado")
    @Story("Guardar médico")
    @Description("Debe manejar correctamente los errores que ocurren durante el guardado")
    public void saveMedico_ErrorDuringSave() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(true));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        Mockito.when(medicoRepository.save(Mockito.any(Medico.class)))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.saveMedico(medicoDto))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException)
                .verify();
    }

    @Test
    @DisplayName("Actualizar médico - Caso exitoso")
    @Story("Actualizar médico")
    @Description("Debe actualizar un médico existente correctamente cuando todos los datos son válidos")
    public void updateMedico_Success() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(true));

        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        Mockito.when(medicoRepository.save(Mockito.any(Medico.class)))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenReturn(medicoDto);

        // Act (When)
        Mono<MedicoDto> result = medicoService.updateMedico(1L, medicoDto);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNext(medicoDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("Actualizar médico - Error por ID o médico nulos")
    @Story("Actualizar médico")
    @Description("Debe manejar correctamente el error cuando el ID o el médico son nulos")
    public void updateMedico_NullIdOrMedico() {
        // Caso 1: ID nulo
        StepVerifier.create(medicoService.updateMedico(null, medicoDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("Médico o ID no pueden ser nulos"))
                .verify();

        // Caso 2: Médico nulo
        StepVerifier.create(medicoService.updateMedico(1L, null))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("Médico o ID no pueden ser nulos"))
                .verify();
    }

    @Test
    @DisplayName("Actualizar médico - Error por datos inválidos")
    @Story("Actualizar médico")
    @Description("Debe manejar correctamente el error cuando los datos del médico son inválidos")
    public void updateMedico_InvalidData() {
        // Arrange (Given)
        MedicoDto invalidMedicoDto = new MedicoDto();
        // No establecemos datos requeridos para que falle la validación

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.updateMedico(1L, invalidMedicoDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("Datos de médico inválidos"))
                .verify();
    }

    @Test
    @DisplayName("Actualizar médico - Error por especialidad inexistente")
    @Story("Actualizar médico")
    @Description("Debe manejar correctamente el error cuando la especialidad no existe")
    public void updateMedico_SpecialtyNotFound() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(false));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.updateMedico(1L, medicoDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ResourceNotFoundException && 
                    throwable.getMessage().contains("Especialidad"))
                .verify();
    }

    @Test
    @DisplayName("Actualizar médico - Error por médico inexistente")
    @Story("Actualizar médico")
    @Description("Debe manejar correctamente el error cuando el médico a actualizar no existe")
    public void updateMedico_MedicoNotFound() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(true));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.empty());

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.updateMedico(1L, medicoDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ResourceNotFoundException && 
                    throwable.getMessage().contains("Médico"))
                .verify();
    }

    @Test
    @DisplayName("Actualizar médico - Error durante la actualización")
    @Story("Actualizar médico")
    @Description("Debe manejar correctamente los errores que ocurren durante la actualización")
    public void updateMedico_ErrorDuringUpdate() {
        // Arrange (Given)
        Mockito.when(especialidadService.existsById(medicoDto.getEspecialidadId()))
                .thenReturn(Mono.just(true));

        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoMapper.toEntity(Mockito.any(MedicoDto.class)))
                .thenReturn(medico);

        Mockito.when(medicoRepository.save(Mockito.any(Medico.class)))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.updateMedico(1L, medicoDto))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException)
                .verify();
    }

    @Test
    @DisplayName("Eliminar médico - Caso exitoso")
    @Story("Eliminar médico")
    @Description("Debe eliminar un médico existente correctamente")
    public void deleteMedico_Success() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoRepository.deleteById(1L))
                .thenReturn(Mono.empty());

        // Act (When)
        Mono<Void> result = medicoService.deleteMedico(1L);

        // Assert (Then)
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Eliminar médico - Error por ID nulo")
    @Story("Eliminar médico")
    @Description("Debe manejar correctamente el error cuando el ID es nulo")
    public void deleteMedico_NullId() {
        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.deleteMedico(null))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("El ID no puede ser nulo"))
                .verify();
    }

    @Test
    @DisplayName("Eliminar médico - Error por médico inexistente")
    @Story("Eliminar médico")
    @Description("Debe manejar correctamente el error cuando el médico a eliminar no existe")
    public void deleteMedico_MedicoNotFound() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.empty());

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.deleteMedico(1L))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ResourceNotFoundException && 
                    throwable.getMessage().contains("Médico"))
                .verify();
    }

    @Test
    @DisplayName("Eliminar médico - Error durante la eliminación")
    @Story("Eliminar médico")
    @Description("Debe manejar correctamente los errores que ocurren durante la eliminación")
    public void deleteMedico_ErrorDuringDelete() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoRepository.deleteById(1L))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.deleteMedico(1L))
                .verifyComplete(); // Para cualquier error que no sea ResourceNotFoundException, el método devuelve Mono.empty()
    }

    @Test
    @DisplayName("Eliminar médico - Error ResourceNotFoundException durante la eliminación")
    @Story("Eliminar médico")
    @Description("Debe manejar correctamente cuando ocurre un ResourceNotFoundException durante la eliminación")
    public void deleteMedico_ResourceNotFoundDuringDelete() {
        // Arrange (Given)
        Mockito.when(medicoRepository.findById(1L))
                .thenReturn(Mono.just(medico));

        Mockito.when(medicoRepository.deleteById(1L))
                .thenReturn(Mono.error(new ResourceNotFoundException("Médico", "id", 1L)));

        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.deleteMedico(1L))
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException)
                .verify(); // Para ResourceNotFoundException, el método propaga el error
    }

    @Test
    @DisplayName("Obtener médicos paginados")
    @Story("Obtener médicos paginados")
    @Description("Debe obtener médicos paginados correctamente")
    public void getMedicosPaginados_Success() {
        // Arrange (Given)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        
        Mockito.when(medicoRepository.findAllPagedOrderByIdAsc(10, 0))
                .thenReturn(Flux.just(medico));

        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenReturn(medicoDto);

        // Act (When)
        Mono<PageResponseDto<MedicoDto>> result = medicoService.getMedicosPaginados(null, null, pageable);

        // Assert (Then)
        StepVerifier.create(result)
                .expectNextMatches(pageResponse -> {
                    return pageResponse.getPageNumber() == 0 &&
                           pageResponse.getPageSize() == 10 &&
                           pageResponse.getContent().size() == 1 &&
                           pageResponse.getContent().get(0).equals(medicoDto);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médicos paginados - Filtrado por nombre y especialidad")
    @Story("Obtener médicos paginados")
    @Description("Debe filtrar correctamente por nombre y especialidad")
    public void getMedicosPaginados_FilterByNameAndSpecialty() {
        // Arrange (Given)
        String nombre = "Dr. Test";
        Long especialidadId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        
        List<Medico> medicos = new ArrayList<>();
        medicos.add(medico);
        
        Mockito.when(medicoRepository.findByNombreAndEspecialidadIdOrderByIdAsc(
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(Flux.fromIterable(medicos));
        
        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenReturn(medicoDto);
        
        // Act (When)
        Mono<PageResponseDto<MedicoDto>> result = medicoService.getMedicosPaginados(nombre, especialidadId, pageable);
        
        // Assert (Then)
        StepVerifier.create(result)
                .assertNext(page -> {
                    assertEquals(1, page.getContent().size());
                    assertEquals(0, page.getPageNumber());
                    assertEquals(10, page.getPageSize());
                    assertEquals(medicoDto, page.getContent().get(0));
                    assertTrue(page.isFirst());
                    assertTrue(page.isLast());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médicos paginados - Error con pageable nulo")
    @Story("Obtener médicos paginados")
    @Description("Debe manejar correctamente el error cuando pageable es nulo")
    public void getMedicosPaginados_NullPageable() {
        // Arrange (Given)
        String nombre = "Dr. Test";
        Long especialidadId = 1L;
        Pageable pageable = null;
        
        // Act & Assert (When & Then)
        StepVerifier.create(medicoService.getMedicosPaginados(nombre, especialidadId, pageable))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Obtener médicos paginados - Múltiples páginas")
    @Story("Obtener médicos paginados")
    @Description("Debe manejar correctamente múltiples páginas de resultados")
    public void getMedicosPaginados_MultiplePages() {
        // Arrange (Given)
        String nombre = null;
        Long especialidadId = null;
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "id")); // Segunda página
        
        List<Medico> medicos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Medico m = new Medico();
            m.setId((long) (i + 6)); // IDs 6-10 para la segunda página
            m.setNombre("Dr. " + i);
            m.setEspecialidadId(1L);
            medicos.add(m);
        }
        
        Mockito.when(medicoRepository.findAllPagedOrderByIdAsc(Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(Flux.fromIterable(medicos));
        
        Mockito.when(medicoMapper.toDto(Mockito.any(Medico.class)))
                .thenAnswer(invocation -> {
                    Medico m = invocation.getArgument(0);
                    MedicoDto dto = new MedicoDto();
                    dto.setId(m.getId());
                    dto.setNombre(m.getNombre());
                    dto.setEspecialidadId(m.getEspecialidadId());
                    return dto;
                });
        
        // Act (When)
        Mono<PageResponseDto<MedicoDto>> result = medicoService.getMedicosPaginados(nombre, especialidadId, pageable);
        
        // Assert (Then)
        StepVerifier.create(result)
                .assertNext(page -> {
                    assertEquals(5, page.getContent().size());
                    assertEquals(1, page.getPageNumber());
                    assertEquals(5, page.getPageSize());
                    assertFalse(page.isFirst());
                    assertFalse(page.isLast());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Obtener médicos paginados - Sin resultados")
    @Story("Obtener médicos paginados")
    @Description("Debe manejar correctamente cuando no hay resultados")
    public void getMedicosPaginados_NoResults() {
        // Arrange (Given)
        String nombre = "Dr. Inexistente";
        Long especialidadId = 999L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        
        Mockito.when(medicoRepository.findByNombreAndEspecialidadIdOrderByIdAsc(
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(Flux.empty());
        
        // Act (When)
        Mono<PageResponseDto<MedicoDto>> result = medicoService.getMedicosPaginados(nombre, especialidadId, pageable);
        
        // Assert (Then)
        StepVerifier.create(result)
                .assertNext(page -> {
                    assertEquals(0, page.getContent().size());
                    assertEquals(0, page.getPageNumber());
                    assertEquals(10, page.getPageSize());
                    assertTrue(page.isFirst());
                    assertTrue(page.isLast());
                })
                .verifyComplete();
    }
}
