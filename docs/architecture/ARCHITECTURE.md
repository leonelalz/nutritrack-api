# Arquitectura del Sistema NutriTrack API 🏗️

## Descripción General

NutriTrack API está construida siguiendo una **arquitectura en capas** (Layered Architecture) con principios de **Domain-Driven Design (DDD)** y patrones **RESTful**.

## 📐 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENTE / FRONTEND                    │
└───────────────────────┬─────────────────────────────────┘
                        │ HTTP/HTTPS
                        ↓
┌─────────────────────────────────────────────────────────┐
│                  CAPA DE SEGURIDAD                       │
│  ┌──────────────┐  ┌────────────┐  ┌─────────────┐     │
│  │ CORS Filter  │→ │ JWT Filter │→ │   Spring    │     │
│  │              │  │            │  │  Security   │     │
│  └──────────────┘  └────────────┘  └─────────────┘     │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────┐
│              CAPA DE CONTROLADORES (REST)                │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │    Auth     │  │   Usuario    │  │  Etiquetas   │   │
│  │ Controller  │  │  Controller  │  │  Controller  │   │
│  └──────┬──────┘  └──────┬───────┘  └──────┬───────┘   │
│         │DTO           │DTO              │DTO          │
└─────────┼──────────────┼─────────────────┼─────────────┘
          │              │                 │
          ↓              ↓                 ↓
┌─────────────────────────────────────────────────────────┐
│              CAPA DE SERVICIOS (LÓGICA)                  │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │    Auth     │  │   Usuario    │  │  Etiquetas   │   │
│  │   Service   │  │   Service    │  │   Service    │   │
│  └──────┬──────┘  └──────┬───────┘  └──────┬───────┘   │
│         │              │                 │              │
└─────────┼──────────────┼─────────────────┼─────────────┘
          │              │                 │
          ↓              ↓                 ↓
┌─────────────────────────────────────────────────────────┐
│           CAPA DE REPOSITORIOS (PERSISTENCIA)            │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ CuentaAuth  │  │ PerfilUsuario│  │  Etiqueta    │   │
│  │ Repository  │  │  Repository  │  │  Repository  │   │
│  └──────┬──────┘  └──────┬───────┘  └──────┬───────┘   │
└─────────┼──────────────┼─────────────────┼─────────────┘
          │              │                 │
          ↓              ↓                 ↓
┌─────────────────────────────────────────────────────────┐
│                  CAPA DE DATOS (JPA)                     │
│                      MySQL/PostgreSQL                    │
└─────────────────────────────────────────────────────────┘
```

## 🧱 Capas del Sistema

### 1. Capa de Seguridad
**Responsabilidad:** Autenticación, autorización y protección

**Componentes:**
- `CorsConfig` - Configuración de CORS
- `SecurityConfig` - Configuración de Spring Security
- `JwtAuthenticationFilter` - Filtro JWT
- `JwtUtil` - Utilidades JWT
- `UserDetailsServiceImpl` - Carga de detalles de usuario

**Flujo:**
```
Request → CORS Filter → JWT Filter → Security Context → Controller
```

### 2. Capa de Controladores (Presentation)
**Responsabilidad:** Exponer endpoints REST y validar requests

**Componentes:**
- `@RestController` - Anotación de controlador
- `@RequestMapping` - Mapeo de rutas
- DTOs (Request/Response) - Transferencia de datos
- `@Valid` - Validaciones automáticas

**Características:**
- Validación de entrada
- Manejo de respuestas HTTP
- Documentación de API
- Transformación DTO ↔ Entity

### 3. Capa de Servicios (Business Logic)
**Responsabilidad:** Lógica de negocio y orquestación

**Componentes:**
- `@Service` - Anotación de servicio
- Validaciones de negocio
- Transacciones
- Coordinación entre repositorios

**Características:**
- Reglas de negocio
- Validaciones complejas
- Manejo de transacciones
- Lógica reutilizable

### 4. Capa de Repositorios (Data Access)
**Responsabilidad:** Acceso y persistencia de datos

**Componentes:**
- `JpaRepository` - Interfaz base
- Métodos de consulta personalizados
- Especificaciones JPA
- `@Query` - Consultas JPQL

### 5. Capa de Modelo (Domain)
**Responsabilidad:** Representación del dominio

**Componentes:**
- Entidades JPA (`@Entity`)
- Enumeraciones
- Relaciones entre entidades
- Value Objects

## 🔄 Patrones de Diseño Utilizados

### Repository Pattern
Abstrae la lógica de acceso a datos.

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### DTO Pattern
Separa la representación de datos de las entidades.

```java
@Data
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    // No incluye password ni datos sensibles
}
```

### Builder Pattern (vía Lombok)
Construcción fluida de objetos.

```java
@Builder
Usuario usuario = Usuario.builder()
    .nombre("Juan")
    .email("juan@example.com")
    .build();
```

### Exception Handler Pattern
Manejo centralizado de excepciones.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND).body(ex.getMessage());
    }
}
```

## 🔐 Flujo de Autenticación

```
1. Cliente → POST /auth/login {email, password}
2. AuthController → AuthService
3. AuthService → UserDetailsService
4. Validar credenciales
5. Generar JWT token
6. Retornar token al cliente
7. Cliente incluye token en headers: Authorization: Bearer {token}
8. JwtFilter valida token en cada request
9. Spring Security autoriza acceso
```

## 📊 Modelo de Datos Simplificado

```
┌──────────────┐
│ CuentaAuth   │
├──────────────┤
│ id           │
│ email        │──┐
│ password     │  │
│ activo       │  │ 1:1
└──────────────┘  │
                  │
                  ↓
┌──────────────────┐
│ PerfilUsuario    │
├──────────────────┤
│ id               │──┐
│ nombre           │  │
│ apellido         │  │ 1:1
│ fecha_nac        │  │
└──────────────────┘  │
                      ↓
┌────────────────────────┐
│ UsuarioPerfilSalud     │
├────────────────────────┤
│ id                     │
│ objetivo               │
│ nivel_actividad        │
└────────────────────────┘

┌──────────────────────┐
│ UsuarioHistorialMed  │
├──────────────────────┤
│ id                   │
│ peso                 │
│ altura               │
│ fecha_registro       │
└──────────────────────┘

┌──────────────┐       ┌────────────────────┐
│  Etiqueta    │       │ EtiquetaIngrediente│
├──────────────┤       ├────────────────────┤
│ id           │←──────│ etiqueta_id        │
│ nombre       │  N:M  │ ingrediente_id     │
│ tipo         │       └────────────────────┘
└──────────────┘
```

## 🚀 Flujo de Desarrollo

### 1. Crear Nueva Funcionalidad

```
1. Diseñar modelo de datos (Entity)
2. Crear Repository
3. Implementar Service con lógica de negocio
4. Crear DTOs (Request/Response)
5. Implementar Controller
6. Añadir validaciones
7. Escribir tests
8. Documentar
```

### 2. Ejemplo Completo

```java
// 1. Entity
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
}

// 2. Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContaining(String nombre);
}

// 3. Service
@Service
public class ProductoService {
    @Autowired
    private ProductoRepository repository;
    
    public Producto crear(Producto producto) {
        // Validaciones de negocio
        return repository.save(producto);
    }
}

// 4. Controller
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoService service;
    
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(service.crear(request.toEntity()));
    }
}
```

## 🔧 Configuración

### application.properties

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/nutritrack_db
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Logging
logging.level.com.nutritrack=DEBUG
```

## 📈 Escalabilidad

### Horizontalmente
- Múltiples instancias con load balancer
- Base de datos con réplicas de lectura
- Cache distribuido (Redis)

### Verticalmente
- Optimización de queries
- Índices en base de datos
- Pool de conexiones

## 🧪 Testing Strategy

### Tests Unitarios
- Servicios con mocks
- Validaciones de negocio
- Métodos de utilidad

### Tests de Integración
- Controladores con MockMvc
- Repositorios con base de datos en memoria
- Flujos completos

## 📚 Decisiones Arquitectónicas

### ¿Por qué Arquitectura en Capas?
- ✅ Separación clara de responsabilidades
- ✅ Facilita testing
- ✅ Mantenibilidad
- ✅ Escalabilidad

### ¿Por qué DTOs?
- ✅ Seguridad (no exponer entidades directamente)
- ✅ Flexibilidad en representación
- ✅ Versionado de API

### ¿Por qué JWT?
- ✅ Stateless authentication
- ✅ Escalabilidad
- ✅ Compatible con microservicios

## 🔮 Futuras Mejoras

- [ ] Implementar caché con Redis
- [ ] Documentación con Swagger/OpenAPI
- [ ] Event-driven architecture con Kafka
- [ ] Migrar a microservicios
- [ ] Implementar CQRS

---

**Última actualización:** Octubre 2025  
**Versión:** 1.0  
**Mantenedor:** Equipo NutriTrack
