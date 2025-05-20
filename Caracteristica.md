-# Patrones de Diseño y Tecnologías en nuestra API Reactiva

## Tabla de Patrones de Diseño con Descripciones Mejoradas

| Familia | Patrón | Principio SOLID | Código/Método | ¿Por qué lo usamos? |
|---------|--------|----------------|---------------|---------------------|
| Creacional | Builder | SRP | `PageResponseDto.<MedicoDto>builder()...build()` | **Descripción**: Separa la construcción de un objeto complejo de su representación.<br>**Principio**: Cada clase tiene una sola responsabilidad - la clase se ocupa solo de sus datos, el builder de construirla.<br>**Ventaja**: Construye objetos paso a paso sin constructores gigantes, permitiendo diferentes configuraciones. |
| Creacional | Inyección de Dependencias | DIP | `private final MedicoService medicoService;` con `@RequiredArgsConstructor` | **Descripción**: En lugar de crear dependencias, las recibimos ya instanciadas.<br>**Principio**: Dependemos de abstracciones (interfaces), no de implementaciones concretas.<br>**Ventaja**: Facilita pruebas unitarias y permite cambiar implementaciones sin modificar el código que las usa. |
| Estructural | DTO | SRP | `MedicoDto`, `ResponseMedico` | **Descripción**: Objetos simples para transportar datos entre procesos.<br>**Principio**: Separa la responsabilidad de transferir datos de la lógica de negocio.<br>**Ventaja**: Protege entidades internas, permite evolucionar API y dominio independientemente. |
| Estructural | Facade | SRP, ISP | `MedicoService` como interfaz | **Descripción**: Proporciona una interfaz simplificada a un subsistema complejo.<br>**Principio**: Ofrece solo métodos que el cliente necesita (ISP) y encapsula complejidad (SRP).<br>**Ventaja**: Desacopla clientes del subsistema y oculta complejidades. |
| Estructural | Adapter/Mapper | OCP | `IMedicoMapper` | **Descripción**: Convierte interfaz de una clase en otra que el cliente espera.<br>**Principio**: Extendemos funcionalidad sin modificar código original.<br>**Ventaja**: Hace trabajar juntas clases con interfaces incompatibles. |
| Comportamiento | Strategy | OCP, DIP | `getFilteredMedicosWithSort()` | **Descripción**: Define familia de algoritmos intercambiables.<br>**Principio**: Podemos añadir nuevas estrategias sin modificar código existente.<br>**Ventaja**: Selecciona comportamiento en tiempo de ejecución según contexto. |
| Comportamiento | Observer | SRP, OCP | `doOnSuccess()`, `doOnError()` | **Descripción**: Define dependencia uno-a-muchos donde cambios notifican a observadores.<br>**Principio**: Separamos la lógica de observación de la operación principal.<br>**Ventaja**: Desacopla componentes, permitiendo reaccionar a eventos sin estar fuertemente acoplados. |
| Comportamiento | Chain of Responsibility | SRP, OCP | `switchIfEmpty().flatMap()` | **Descripción**: Pasa solicitudes a lo largo de cadena de handlers.<br>**Principio**: Cada eslabón tiene responsabilidad única y podemos añadir nuevos sin modificar existentes.<br>**Ventaja**: Cada eslabón decide procesar la solicitud o pasarla al siguiente. |
| Comportamiento | Template Method | OCP | Métodos en `ReactiveCrudRepository` | **Descripción**: Define esqueleto de algoritmo dejando algunos pasos para subclases.<br>**Principio**: Extendemos comportamiento sin modificar estructura general.<br>**Ventaja**: Reutiliza código común permitiendo variaciones específicas. |
| Comportamiento | Command | SRP | Operadores de Project Reactor | **Descripción**: Encapsula solicitud como objeto.<br>**Principio**: Separa responsabilidad de solicitar operación de cómo se ejecuta.<br>**Ventaja**: Parametriza clientes con diferentes solicitudes y soporta operaciones deshacer. |
| Estructural | Composite | SRP, OCP | Combinación de `Flux` | **Descripción**: Compone objetos en estructuras de árbol para tratar individual/grupalmente.<br>**Principio**: Trata conjunto de objetos con misma interfaz que objeto único.<br>**Ventaja**: Simplifica código cliente cuando maneja jerarquías de objetos. |
| Comportamiento | Reactive Pattern | SRP, OCP | Uso de `Mono` y `Flux` | **Descripción**: Procesa flujos de datos asincrónicos con propagación de cambios.<br>**Principio**: Separa generación de eventos del consumo y extensible a nuevos operadores.<br>**Ventaja**: Manejo eficiente de recursos con procesamiento no bloqueante. |
| Estructural | Repository | DIP | `MedicoRepository` | **Descripción**: Abstrae y encapsula acceso a fuentes de datos.<br>**Principio**: Dependemos de interfaces, no implementaciones concretas de acceso a datos.<br>**Ventaja**: Centraliza lógica de acceso a datos, facilita testing y cambios en persistencia. |
| Comportamiento | Specification | OCP | Queries con filtros dinámicos | **Descripción**: Encapsula reglas de negocio como objetos combinables.<br>**Principio**: Añade nuevas especificaciones sin modificar existentes.<br>**Ventaja**: Crea consultas complejas combinando especificaciones simples. |
| Estructural | Module | SRP, DIP | Paquetes organizados | **Descripción**: Agrupa componentes relacionados.<br>**Principio**: Cada módulo tiene responsabilidad única y depende de abstracciones.<br>**Ventaja**: Mejora organización, mantenibilidad y permite desarrollo en paralelo. |

## Resumen de Patrones por Categoría

### Por Familia

| Familia | Patrones Utilizados |
|---------|---------------------|
| **Creacional** | • Builder<br>• Inyección de Dependencias |
| **Estructural** | • DTO<br>• Facade<br>• Adapter/Mapper<br>• Composite<br>• Repository<br>• Module |
| **Comportamiento** | • Strategy<br>• Observer<br>• Chain of Responsibility<br>• Template Method<br>• Command<br>• Reactive Pattern<br>• Specification |

### Por Principio SOLID

| Principio SOLID | Patrones que lo Aplican |
|-----------------|-------------------------|
| **SRP** (Responsabilidad Única) | • Builder<br>• DTO<br>• Facade<br>• Observer<br>• Chain of Responsibility<br>• Command<br>• Composite<br>• Reactive Pattern<br>• Module |
| **OCP** (Abierto/Cerrado) | • Adapter/Mapper<br>• Strategy<br>• Observer<br>• Chain of Responsibility<br>• Template Method<br>• Composite<br>• Reactive Pattern<br>• Specification |
| **LSP** (Sustitución de Liskov) | • Repository (implementaciones)<br>• Strategy (al usar diferentes estrategias) |
| **ISP** (Segregación de Interfaces) | • Facade<br>• Repository |
| **DIP** (Inversión de Dependencias) | • Inyección de Dependencias<br>• Strategy<br>• Repository<br>• Module |

### Por Tipo de Problema Resuelto

| Problema | Patrones que lo Resuelven |
|----------|---------------------------|
| **Creación de objetos** | • Builder<br>• Inyección de Dependencias |
| **Desacoplamiento** | • DTO<br>• Facade<br>• Adapter/Mapper<br>• Observer<br>• Repository |
| **Composición** | • Composite<br>• Chain of Responsibility<br>• Reactive Pattern |
| **Extensibilidad** | • Strategy<br>• Template Method<br>• Specification<br>• Command |
| **Organización de código** | • Module<br>• Repository |
| **Programación asíncrona** | • Reactive Pattern<br>• Observer |

## Descripción de los Principios SOLID

| Principio | Descripción | Ejemplo en nuestro código |
|-----------|-------------|---------------------------|
| **SRP** (Principio de Responsabilidad Única) | Cada clase debe tener una única razón para cambiar. | Nuestro `MedicoMapper` solo se encarga de conversiones entre entidad y DTO. Si cambian reglas de mapeo, solo modificamos esta clase. |
| **OCP** (Principio Abierto/Cerrado) | Las entidades deben estar abiertas para extensión, pero cerradas para modificación. | Podemos añadir nuevas estrategias de ordenamiento sin modificar el código existente, solo agregando nuevos métodos en el repositorio. |
| **LSP** (Principio de Sustitución de Liskov) | Los objetos de una clase pueden ser reemplazados por objetos de sus subclases sin afectar la funcionalidad. | Cualquier implementación de `MedicoService` debe poder usarse donde se espera esta interfaz sin problemas. |
| **ISP** (Principio de Segregación de Interfaces) | Los clientes no deben depender de interfaces que no usan. | Definimos interfaces específicas como `IMedicoMapper` con solo los métodos necesarios en lugar de interfaces gigantes con métodos irrelevantes. |
| **DIP** (Principio de Inversión de Dependencias) | Depender de abstracciones, no concreciones. | Nuestro controlador depende de `MedicoService` (interfaz), no de su implementación concreta `MedicoServiceImpl`. |

## Tecnologías Utilizadas

### Frameworks y Librerías Principales

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Spring Boot | 2.6.3 | Framework base que proporciona configuración por defecto y autoconfiguración |
| Spring WebFlux | Incluido en Boot | Framework web reactivo para construir APIs no bloqueantes |
| Project Reactor | Incluido en Boot | Biblioteca para programación reactiva (Mono/Flux) |
| Spring Data R2DBC | Incluido en Boot | Acceso a datos reactivo para bases de datos relacionales |
| R2DBC PostgreSQL | 0.8.13.RELEASE | Driver R2DBC específico para PostgreSQL |
| Lombok | Última | Reduce código repetitivo con anotaciones (@Data, @Builder, etc.) |
| SpringDoc OpenAPI | 1.6.6 | Genera documentación de API basada en OpenAPI (Swagger) |
| JUnit 5 | 5.10.2 | Framework de pruebas unitarias |
| AssertJ | Incluido en Boot | Aserciones fluidas para pruebas |

### Base de Datos

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| PostgreSQL | Última | Sistema de gestión de base de datos relacional |
| R2DBC | 0.8.x | API reactiva para acceso a bases de datos relacionales |

## Ventajas de nuestro diseño reactivo

1. **Alta Concurrencia**: Podemos manejar miles de conexiones simultáneas con recursos limitados
2. **Uso Eficiente de Recursos**: Sin bloqueos, aprovechamos mejor los hilos y memoria disponibles
3. **Resiliencia**: Mejor manejo de fallos con operadores como onErrorResume, retry, etc.
4. **Composición**: Fácil combinación de operaciones asíncronas complejas
5. **Rendimiento**: Optimizaciones como paginación sin conteo para mejor desempeño con grandes volúmenes de datos