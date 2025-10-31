# Guía de Contribución 🤝

Gracias por contribuir a NutriTrack API. Esta guía te ayudará a mantener la calidad y consistencia del código.

## 📋 Tabla de Contenidos

- [Configuración Inicial](#configuración-inicial)
- [Flujo de Trabajo Git](#flujo-de-trabajo-git)
- [Estándares de Código](#estándares-de-código)
- [Convenciones de Nombres](#convenciones-de-nombres)
- [Estructura de Commits](#estructura-de-commits)
- [Pull Requests](#pull-requests)
- [Testing](#testing)
- [Documentación](#documentación)

## 🔧 Configuración Inicial

### 1. Fork y Clone

```bash
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api
git checkout development
```

### 2. Configurar Git

```bash
git config user.name "Tu Nombre"
git config user.email "tu.email@example.com"
```

### 3. Instalar Dependencias

```bash
./mvnw clean install
```

## 🌿 Flujo de Trabajo Git

### Ramas

El proyecto usa **Git Flow**:

- `main` - Producción (protegida)
- `development` - Desarrollo principal (protegida)
- `feature/nombre-feature` - Nuevas funcionalidades
- `bugfix/nombre-bug` - Corrección de bugs
- `hotfix/nombre-hotfix` - Correcciones urgentes en producción
- `release/x.y.z` - Preparación de releases

### Crear una Nueva Feature

```bash
# Actualizar development
git checkout development
git pull origin development

# Crear rama de feature
git checkout -b feature/nombre-descriptivo

# Trabajar en tu feature...
git add .
git commit -m "feat: descripción del cambio"

# Subir cambios
git push origin feature/nombre-descriptivo
```

### Sincronizar con Development

```bash
# En tu rama de feature
git checkout development
git pull origin development
git checkout feature/nombre-descriptivo
git merge development

# Resolver conflictos si existen
# Después hacer push
git push origin feature/nombre-descriptivo
```

## 💻 Estándares de Código

### Formato Java

#### Indentación
- 4 espacios (no tabs)
- Llaves en la misma línea (K&R style)

```java
public class MiClase {
    private String nombre;
    
    public void metodo() {
        if (condicion) {
            // código
        }
    }
}
```

#### Imports
- No usar imports con `*`
- Agrupar imports: java, javax, spring, otros, proyecto

```java
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nutritrack.nutritrackapi.model.Usuario;
```

### Uso de Lombok

Usar anotaciones de Lombok para reducir boilerplate:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String email;
}
```

### Validaciones

Usar anotaciones de validación en DTOs:

```java
@Data
public class UsuarioRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
}
```

## 📝 Convenciones de Nombres

### Clases

| Tipo | Sufijo | Ejemplo |
|------|--------|---------|
| Controller | Controller | `UsuarioController` |
| Service | Service | `UsuarioService` |
| Repository | Repository | `UsuarioRepository` |
| DTO Request | Request | `CrearUsuarioRequest` |
| DTO Response | Response | `UsuarioResponse` |
| Entity | - | `Usuario` |
| Exception | Exception | `ResourceNotFoundException` |
| Config | Config | `SecurityConfig` |

### Métodos

```java
// CRUD básico
findById()
findAll()
save()
update()
deleteById()

// Búsquedas
findByEmail()
findByNombreContaining()
existsByEmail()

// Validaciones
validateUsuario()
checkPermissions()

// Conversiones
toEntity()
toResponse()
mapToDTO()
```

### Variables

```java
// camelCase para variables
private String nombreUsuario;
private List<Usuario> listaUsuarios;

// UPPER_CASE para constantes
private static final String JWT_SECRET = "secret";
private static final int MAX_ATTEMPTS = 3;
```

## 📦 Estructura de Commits

Usar **Conventional Commits**:

```
<tipo>(<alcance>): <descripción>

[cuerpo opcional]

[footer opcional]
```

### Tipos de Commits

- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Formato, sin cambios de código
- `refactor`: Refactorización de código
- `test`: Añadir o modificar tests
- `chore`: Tareas de mantenimiento

### Ejemplos

```bash
# Feature
git commit -m "feat(auth): añadir autenticación con JWT"

# Bug fix
git commit -m "fix(usuario): corregir validación de email"

# Documentación
git commit -m "docs(readme): actualizar guía de instalación"

# Refactorización
git commit -m "refactor(service): simplificar lógica de validación"

# Test
git commit -m "test(auth): añadir tests para login"
```

## 🔄 Pull Requests

### Antes de Crear un PR

✅ **Checklist:**

- [ ] Código compila sin errores
- [ ] Todos los tests pasan
- [ ] Código sigue los estándares
- [ ] Sin código comentado innecesario
- [ ] Sin logs de debug
- [ ] Documentación actualizada
- [ ] Commits descriptivos

### Template de PR

Usar el template automático de `.github/pull_request_template.md`:

```markdown
## Descripción
Breve descripción de los cambios

## Tipo de Cambio
- [ ] Nueva feature
- [ ] Bug fix
- [ ] Refactorización
- [ ] Documentación

## Módulo Afectado
- [ ] Autenticación
- [ ] Perfil Usuario
- [ ] Salud/Historial
- [ ] Etiquetas
- [ ] Infraestructura

## Testing
- [ ] Tests unitarios añadidos/actualizados
- [ ] Tests de integración verificados
- [ ] Pruebas manuales realizadas

## Checklist
- [ ] Código revisado
- [ ] Sin warnings
- [ ] Documentación actualizada
```

### Revisión de Código

**Todo PR requiere:**
- ✅ Al menos 1 aprobación
- ✅ CI/CD pipeline exitoso
- ✅ Sin conflictos con development

## 🧪 Testing

### Tests Unitarios

```java
@SpringBootTest
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private UsuarioService usuarioService;
    
    @Test
    void testFindById_Success() {
        // Given
        Long id = 1L;
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(id))
            .thenReturn(Optional.of(usuario));
        
        // When
        Usuario result = usuarioService.findById(id);
        
        // Then
        assertNotNull(result);
        verify(usuarioRepository).findById(id);
    }
}
```

### Cobertura de Tests

- **Mínimo requerido:** 70%
- **Objetivo:** 80%+

Ejecutar reporte de cobertura:

```bash
./mvnw test jacoco:report
```

## 📚 Documentación

### Documentar tu Módulo

Al trabajar en un módulo, actualizar:

1. **`docs/modules/[tu-modulo].md`** - Documentación específica
2. **JavaDoc** en clases y métodos públicos
3. **README.md** si afecta configuración general

### JavaDoc

```java
/**
 * Servicio para gestión de usuarios.
 * Proporciona operaciones CRUD y validaciones de negocio.
 * 
 * @author Tu Nombre
 * @version 1.0
 */
@Service
public class UsuarioService {
    
    /**
     * Busca un usuario por su ID.
     * 
     * @param id Identificador del usuario
     * @return Usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
```

## ❓ Preguntas

Si tienes dudas:
1. Consulta la documentación en `docs/`
2. Revisa ejemplos en el código existente
3. Pregunta al responsable de tu módulo
4. Crea un issue en GitHub con la etiqueta `question`

## 🚫 Qué NO Hacer

- ❌ Commit directo a `main` o `development`
- ❌ Push de credenciales o datos sensibles
- ❌ Código sin tests
- ❌ Ignorar warnings del compilador
- ❌ Dejar código comentado
- ❌ Usar `System.out.println()` para logs

## ✅ Mejores Prácticas

- ✔️ Commits pequeños y frecuentes
- ✔️ Nombres descriptivos
- ✔️ Un propósito por commit
- ✔️ Tests antes de PR
- ✔️ Documentar decisiones importantes
- ✔️ Pedir revisión cuando tengas dudas

---

¡Gracias por contribuir a NutriTrack API! 🎉
