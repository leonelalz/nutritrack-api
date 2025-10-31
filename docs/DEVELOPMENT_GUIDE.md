# Guía de Desarrollo 💻

**Versión:** 1.0  
**Última actualización:** Octubre 2025

## 📋 Tabla de Contenidos

- [Configuración del Entorno](#configuración-del-entorno)
- [Estándares de Código](#estándares-de-código)
- [Estructura de Capas](#estructura-de-capas)
- [Manejo de Excepciones](#manejo-de-excepciones)
- [Patrones Comunes](#patrones-comunes)
- [Base de Datos](#base-de-datos)
- [Testing](#testing)
- [Logging](#logging)
- [Performance](#performance)

## 🔧 Configuración del Entorno

### Instalación de Herramientas

```bash
# Java
java -version  # Debe ser 17+

# Maven
mvn -version   # Debe ser 3.8+

# Git
git --version
```

### Configuración del IDE

#### IntelliJ IDEA

1. **Importar Proyecto**
   - File → Open → Seleccionar pom.xml
   - Marcar "Import as Maven project"

2. **Configurar Lombok**
   - Settings → Plugins → Instalar "Lombok"
   - Settings → Build → Compiler → Annotation Processors
   - Marcar "Enable annotation processing"

3. **Formato de Código**
   - Importar esquema de formato: `.idea/codeStyles/`
   - Settings → Editor → Code Style → Scheme

#### VS Code

Extensiones recomendadas:
```json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "vmware.vscode-spring-boot",
    "gabrielbb.vscode-lombok",
    "sonarsource.sonarlint-vscode"
  ]
}
```

### Variables de Entorno Local

Crear archivo `.env` (no commitear):

```properties
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=nutritrack_db
DB_USERNAME=nutritrack_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-super-secret-key-at-least-256-bits
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080

# Logs
LOGGING_LEVEL=DEBUG
```

## 📐 Estándares de Código

### Nomenclatura

#### Clases

```java
// Entidades: Sustantivo singular
public class Usuario { }
public class Ingrediente { }

// Servicios: Sustantivo + Service
public class UsuarioService { }

// Controladores: Sustantivo + Controller
public class UsuarioController { }

// Repositorios: Sustantivo + Repository
public interface UsuarioRepository { }

// DTOs: Descripción + Request/Response
public class CrearUsuarioRequest { }
public class UsuarioResponse { }

// Excepciones: Descripción + Exception
public class ResourceNotFoundException extends RuntimeException { }
```

#### Métodos

```java
// CRUD básico
public Usuario findById(Long id) { }
public List<Usuario> findAll() { }
public Usuario save(Usuario usuario) { }
public Usuario update(Long id, Usuario usuario) { }
public void deleteById(Long id) { }

// Queries personalizadas
public Optional<Usuario> findByEmail(String email) { }
public List<Usuario> findByNombreContaining(String nombre) { }
public boolean existsByEmail(String email) { }

// Operaciones de negocio
public void activarCuenta(Long id) { }
public void cambiarPassword(Long id, String newPassword) { }
public List<Usuario> buscarActivos() { }

// Validaciones (retornan boolean)
public boolean isEmailValido(String email) { }
public boolean hasPermission(Usuario usuario, String permission) { }

// Conversiones
public UsuarioResponse toResponse(Usuario usuario) { }
public Usuario toEntity(UsuarioRequest request) { }
```

#### Variables

```java
// camelCase para variables locales y campos
private String nombreCompleto;
private LocalDateTime fechaCreacion;
private List<Etiqueta> listaEtiquetas;

// UPPER_CASE para constantes
private static final String DEFAULT_ROLE = "USER";
private static final int MAX_LOGIN_ATTEMPTS = 3;
private static final long SESSION_TIMEOUT_MS = 3600000L;
```

### Formato de Código

```java
// Llaves en la misma línea (K&R style)
public class MiClase {
    
    // Un campo por línea
    private Long id;
    private String nombre;
    
    // Métodos separados por línea en blanco
    public void metodo1() {
        // código
    }
    
    public void metodo2() {
        // código
    }
}

// Condicionales
if (condicion) {
    // código
} else if (otraCondicion) {
    // código
} else {
    // código
}

// Bucles
for (Usuario usuario : usuarios) {
    // código
}

while (condicion) {
    // código
}

// Cadenas de llamadas (fluent API)
List<String> nombres = usuarios.stream()
    .filter(u -> u.isActivo())
    .map(Usuario::getNombre)
    .sorted()
    .collect(Collectors.toList());
```

### Anotaciones

```java
// Orden de anotaciones
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;
}
```

## 🏗️ Estructura de Capas

### 1. Controller Layer

**Responsabilidad:** Manejo de requests HTTP

```java
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<Usuario> usuarios = usuarioService.findAll();
        List<UsuarioResponse> response = usuarios.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        return ResponseEntity.ok(toResponse(usuario));
    }
    
    /**
     * Crear nuevo usuario
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody CrearUsuarioRequest request) {
        Usuario usuario = usuarioService.crear(toEntity(request));
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toResponse(usuario));
    }
    
    /**
     * Actualizar usuario existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioService.update(id, toEntity(request));
        return ResponseEntity.ok(toResponse(usuario));
    }
    
    /**
     * Eliminar usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // Métodos de conversión
    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
            .id(usuario.getId())
            .nombre(usuario.getNombre())
            .email(usuario.getEmail())
            .build();
    }
    
    private Usuario toEntity(CrearUsuarioRequest request) {
        return Usuario.builder()
            .nombre(request.getNombre())
            .email(request.getEmail())
            .build();
    }
}
```

**Buenas prácticas:**
- ✅ Usar DTOs para request/response
- ✅ Validar con `@Valid`
- ✅ Retornar `ResponseEntity` con códigos HTTP apropiados
- ✅ Documentar endpoints con JavaDoc
- ❌ NO poner lógica de negocio en controladores
- ❌ NO acceder directamente a repositorios

### 2. Service Layer

**Responsabilidad:** Lógica de negocio

```java
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    
    /**
     * Buscar usuario por ID
     * @throws ResourceNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado con ID: " + id));
    }
    
    /**
     * Crear nuevo usuario con validaciones
     */
    public Usuario crear(Usuario usuario) {
        log.info("Creando usuario: {}", usuario.getEmail());
        
        // Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new DuplicateResourceException(
                "Ya existe un usuario con ese email");
        }
        
        // Validaciones de negocio
        validarUsuario(usuario);
        
        // Establecer valores por defecto
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        
        // Guardar
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        // Enviar email de bienvenida (async)
        emailService.enviarBienvenida(savedUsuario.getEmail());
        
        log.info("Usuario creado exitosamente: {}", savedUsuario.getId());
        return savedUsuario;
    }
    
    /**
     * Actualizar usuario existente
     */
    public Usuario update(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = findById(id);
        
        // Actualizar campos permitidos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setUltimaActualizacion(LocalDateTime.now());
        
        return usuarioRepository.save(usuarioExistente);
    }
    
    /**
     * Eliminar usuario (soft delete)
     */
    public void deleteById(Long id) {
        Usuario usuario = findById(id);
        
        // Verificar que no esté en uso
        if (tieneRecursosDependientes(usuario)) {
            throw new ResourceInUseException(
                "No se puede eliminar el usuario porque tiene recursos asociados");
        }
        
        // Soft delete
        usuario.setActivo(false);
        usuario.setFechaEliminacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        log.info("Usuario eliminado (soft): {}", id);
    }
    
    // Métodos privados de validación
    private void validarUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        
        if (usuario.getEmail() == null || !isEmailValido(usuario.getEmail())) {
            throw new BusinessRuleException("Email inválido");
        }
    }
    
    private boolean isEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean tieneRecursosDependientes(Usuario usuario) {
        // Verificar dependencias
        return false; // Implementar lógica real
    }
}
```

**Buenas prácticas:**
- ✅ Usar `@Transactional` para operaciones de escritura
- ✅ Usar `@Transactional(readOnly = true)` para lecturas
- ✅ Validar reglas de negocio
- ✅ Logging de operaciones importantes
- ✅ Lanzar excepciones específicas
- ❌ NO retornar null (usar Optional o lanzar excepción)

### 3. Repository Layer

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Query methods derivados
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByActivoTrue();
    
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByEmail(String email);
    
    // Queries JPQL personalizadas
    @Query("SELECT u FROM Usuario u WHERE " +
           "u.activo = true AND " +
           "u.fechaCreacion >= :fechaDesde")
    List<Usuario> findActivosDesde(
        @Param("fechaDesde") LocalDateTime fechaDesde
    );
    
    // Queries nativas (solo cuando sea necesario)
    @Query(value = "SELECT * FROM usuarios u " +
                   "WHERE u.email LIKE %:dominio%", 
           nativeQuery = true)
    List<Usuario> findByDominio(@Param("dominio") String dominio);
    
    // Queries con proyecciones
    @Query("SELECT new com.nutritrack.nutritrackapi.dto.UsuarioSummary(" +
           "u.id, u.nombre, u.email) " +
           "FROM Usuario u WHERE u.activo = true")
    List<UsuarioSummary> findSummaries();
}
```

## ⚠️ Manejo de Excepciones

### Jerarquía de Excepciones

```java
// Excepción base
public class NutritrackException extends RuntimeException {
    public NutritrackException(String message) {
        super(message);
    }
    
    public NutritrackException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Excepciones específicas
public class ResourceNotFoundException extends NutritrackException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class DuplicateResourceException extends NutritrackException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

public class BusinessRuleException extends NutritrackException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

public class ResourceInUseException extends NutritrackException {
    public ResourceInUseException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Duplicate Resource")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Business Rule Violation")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ValidationErrorResponse response = ValidationErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .errors(errors)
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("Ha ocurrido un error inesperado")
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

## 📊 Base de Datos

### Convenciones de Nombres

```sql
-- Tablas: plural, snake_case
CREATE TABLE usuarios (...)
CREATE TABLE perfiles_salud (...)

-- Columnas: snake_case
id, nombre_completo, fecha_creacion

-- Índices: idx_tabla_columna
CREATE INDEX idx_usuarios_email ON usuarios(email);

-- Foreign Keys: fk_tabla_referencia
ALTER TABLE perfiles ADD CONSTRAINT fk_perfil_usuario 
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id);
```

### Migraciones

Usar Flyway o Liquibase para versionado:

```
src/main/resources/db/migration/
├── V1__create_usuarios_table.sql
├── V2__create_roles_table.sql
├── V3__add_telefono_to_usuarios.sql
```

## 🧪 Testing

Ver sección completa en [TESTING_GUIDE.md](TESTING_GUIDE.md)

## 📝 Logging

```java
@Slf4j
public class MiClase {
    
    public void metodo() {
        // Niveles de log
        log.trace("Detalle muy fino");
        log.debug("Información de debug: {}", variable);
        log.info("Operación completada exitosamente");
        log.warn("Advertencia: {}", mensaje);
        log.error("Error procesando request", exception);
        
        // NO usar System.out.println()
        // NO logear información sensible (passwords, tokens)
    }
}
```

## ⚡ Performance

### Optimización de Queries

```java
// MAL: N+1 queries
List<Usuario> usuarios = usuarioRepository.findAll();
for (Usuario u : usuarios) {
    List<Rol> roles = u.getRoles(); // Query adicional por cada usuario
}

// BIEN: Fetch eager con JOIN
@Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
List<Usuario> findAllWithRoles();

// Paginación para datasets grandes
Page<Usuario> usuarios = usuarioRepository.findAll(
    PageRequest.of(0, 20, Sort.by("nombre"))
);
```

### Caché

```java
@Service
public class UsuarioService {
    
    @Cacheable(value = "usuarios", key = "#id")
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(...);
    }
    
    @CacheEvict(value = "usuarios", key = "#id")
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
```

---

**Próxima lectura:** [TESTING_GUIDE.md](TESTING_GUIDE.md)
