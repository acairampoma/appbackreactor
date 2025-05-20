# Guía de Cobertura de Pruebas

Este documento proporciona una guía detallada de la cobertura de pruebas para los métodos del servicio `MedicoService`. Para cada método, se identifican las condiciones que deben probarse y los tests correspondientes.

## Análisis de Cobertura por Método

### 1. `getMedicosPaginados`

**Descripción**: Recupera médicos de forma paginada con opciones de filtrado y ordenamiento.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | Parámetro `pageable` es nulo | `getMedicosPaginados_NullPageable` | ✅ |
| 2 | Sin filtros (nombre y especialidadId nulos) | `getMedicosPaginados_Success` | ✅ |
| 3 | Con filtros por nombre | `getMedicosPaginados_FilterByName` | ✅ |
| 4 | Con filtros por especialidad | `getMedicosPaginados_FilterBySpecialty` | ✅ |
| 5 | Con filtros por nombre y especialidad | `getMedicosPaginados_FilterByNameAndSpecialty` | ✅ |
| 6 | Múltiples páginas (verificar paginación) | `getMedicosPaginados_MultiplePages` | ✅ |
| 7 | Sin resultados | `getMedicosPaginados_NoResults` | ✅ |
| 8 | Error en repositorio | `getMedicosPaginados_Error` | ✅ |

**Cobertura**: 8/8 condiciones (100%)

### 2. `saveMedico`

**Descripción**: Guarda un nuevo médico en el sistema.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | Médico DTO es nulo | `saveMedico_NullMedico` | ✅ |
| 2 | Datos del médico inválidos | `saveMedico_InvalidData` | ✅ |
| 3 | Especialidad no existe | `saveMedico_SpecialtyNotFound` | ✅ |
| 4 | Error durante el guardado | `saveMedico_ErrorDuringSave` | ✅ |
| 5 | Guardado exitoso | `saveMedico_Success` | ✅ |

**Cobertura**: 5/5 condiciones (100%)

### 3. `updateMedico`

**Descripción**: Actualiza un médico existente.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | ID es nulo | `updateMedico_NullIdOrMedico` | ✅ |
| 2 | Médico DTO es nulo | `updateMedico_NullIdOrMedico` | ✅ |
| 3 | Datos del médico inválidos | `updateMedico_InvalidData` | ✅ |
| 4 | Especialidad no existe | `updateMedico_SpecialtyNotFound` | ✅ |
| 5 | Médico a actualizar no existe | `updateMedico_MedicoNotFound` | ✅ |
| 6 | Error durante la actualización | `updateMedico_ErrorDuringUpdate` | ✅ |
| 7 | Actualización exitosa | `updateMedico_Success` | ✅ |

**Cobertura**: 7/7 condiciones (100%)

### 4. `deleteMedico`

**Descripción**: Elimina un médico existente.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | ID es nulo | `deleteMedico_NullId` | ✅ |
| 2 | Médico a eliminar no existe | `deleteMedico_MedicoNotFound` | ✅ |
| 3 | Error durante la eliminación (no ResourceNotFoundException) | `deleteMedico_ErrorDuringDelete` | ✅ |
| 4 | Error ResourceNotFoundException durante la eliminación | `deleteMedico_ResourceNotFoundDuringDelete` | ✅ |
| 5 | Eliminación exitosa | `deleteMedico_Success` | ✅ |

**Cobertura**: 5/5 condiciones (100%)

### 5. `getMedicoById`

**Descripción**: Recupera un médico por su ID.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | ID es nulo | `getMedicoById_NullId` | ❌ |
| 2 | Médico no existe | `getMedicoById_NotFound` | ✅ |
| 3 | Error en repositorio | `getMedicoById_Error` | ✅ |
| 4 | Recuperación exitosa | `getMedicoById_Success` | ✅ |

**Cobertura**: 3/4 condiciones (75%)

### 6. `getMedicoWithEspecialidadById`

**Descripción**: Recupera un médico con su especialidad por ID.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | ID es nulo | `getMedicoWithEspecialidadById_NullId` | ❌ |
| 2 | Médico no existe | `getMedicoWithEspecialidadById_NotFound` | ✅ |
| 3 | Error en repositorio | `getMedicoWithEspecialidadById_Error` | ✅ |
| 4 | Recuperación exitosa | `getMedicoWithEspecialidadById_Success` | ✅ |

**Cobertura**: 3/4 condiciones (75%)

### 7. `getAllMedicosWithEspecialidad`

**Descripción**: Recupera todos los médicos con sus especialidades.

**Condiciones a probar**:

| # | Condición | Test Requerido | Estado |
|---|-----------|----------------|--------|
| 1 | Lista vacía | `getAllMedicosWithEspecialidad_EmptyList` | ❌ |
| 2 | Error en repositorio | `getAllMedicosWithEspecialidad_Error` | ✅ |
| 3 | Recuperación exitosa | `getAllMedicosWithEspecialidad_Success` | ✅ |

**Cobertura**: 2/3 condiciones (67%)

## Resumen de Cobertura

| Método | Condiciones Totales | Condiciones Cubiertas | Porcentaje |
|--------|---------------------|----------------------|------------|
| getMedicosPaginados | 8 | 8 | 100% |
| saveMedico | 5 | 5 | 100% |
| updateMedico | 7 | 7 | 100% |
| deleteMedico | 5 | 5 | 100% |
| getMedicoById | 4 | 3 | 75% |
| getMedicoWithEspecialidadById | 4 | 3 | 75% |
| getAllMedicosWithEspecialidad | 3 | 2 | 67% |
| **Total** | **36** | **33** | **92%** |

## Próximos Pasos

Para alcanzar una cobertura del 100%, se recomienda implementar los siguientes tests:

1. `getMedicoById_NullId`
2. `getMedicoWithEspecialidadById_NullId`
3. `getAllMedicosWithEspecialidad_EmptyList`

## Metodología para Determinar Tests Necesarios

1. **Análisis de Código**: Examinar el código fuente para identificar todas las rutas de ejecución y condiciones.
2. **Identificación de Condiciones**: Para cada método, identificar todas las condiciones que pueden afectar el comportamiento:
   - Validaciones de parámetros
   - Ramificaciones condicionales (if/else)
   - Manejo de errores y excepciones
   - Casos límite
3. **Mapeo de Tests**: Para cada condición identificada, definir un test específico.
4. **Seguimiento de Cobertura**: Mantener esta documentación actualizada a medida que se implementan nuevos tests.

Esta metodología ayuda a garantizar una cobertura de pruebas completa y sistemática, facilitando la identificación de áreas que requieren más atención.
