# Módulo de Autenticación y Seguridad 🔐

**Responsable:** [Nombre del Desarrollador]  
**Última actualización:** Octubre 2025

## 📋 Descripción

Módulo encargado de la autenticación de usuarios, gestión de sesiones mediante JWT, y control de acceso basado en roles.

## 🎯 Responsabilidades

- Registro de nuevas cuentas de usuario
- Autenticación con email y contraseña
- Generación y validación de tokens JWT
- Gestión de roles y permisos
- Renovación de tokens
- Cierre de sesión

## 📁 Estructura de Archivos

```
src/main/java/com/nutritrack/nutritrackapi/
├── controller/
│   ├── AuthController.java              # Endpoints públicos de auth
│   └── CuentaAuthController.java        # Gestión de cuentas (admin)
├── service/
│   ├── AuthService.java                 # Lógica de autenticación
│   └── CuentaAuthService.java          # CRUD de cuentas
├── repository/
│   ├── CuentaAuthRepository.java       # Acceso a datos de cuentas
│   └── RolRepository.java              # Acceso a datos de roles
├── model/
│   ├── CuentaAuth.java                 # Entidad de cuenta
│   └── Rol.java                        # Entidad de rol
├── security/
│   ├── JwtAuthenticationFilter.java    # Filtro JWT
│   ├── JwtUtil.java                    # Utilidades JWT
│   └── UserDetailsServiceImpl.java     # Carga de usuarios
├── config/
│   └── SecurityConfig.java             # Configuración de seguridad
└── dto/
    ├── request/
    │   ├── LoginRequest.java
    │   ├── RegisterRequest.java
    │   └── ChangePasswordRequest.java
    └── response/
        ├── AuthResponse.java
        └── CuentaAuthResponse.java
```

## 🔌 API Endpoints

### Autenticación Pública

#### POST /api/auth/register
Registrar nueva cuenta de usuario.

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "Password123!",
  "confirmPassword": "Password123!"
}
```

**Response (201):**
```json
{
  "message": "Usuario registrado exitosamente",
  "userId": 1
}
```

**Validaciones:**
- Email válido y único
- Password mínimo 8 caracteres, 1 mayúscula, 1 número
- Confirmación de contraseña coincide

---

#### POST /api/auth/login
Iniciar sesión.

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "Password123!"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "email": "usuario@example.com",
    "roles": ["USER"]
  }
}
```

**Errores:**
- 401: Credenciales inválidas
- 403: Cuenta desactivada

---

#### POST /api/auth/refresh
Renovar token JWT.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 86400000
}
```

---

### Gestión de Cuentas (Requiere Autenticación)

#### GET /api/cuentas
Listar todas las cuentas (Admin).

**Response (200):**
```json
[
  {
    "id": 1,
    "email": "usuario@example.com",
    "activo": true,
    "roles": ["USER"],
    "fechaCreacion": "2025-10-31T10:00:00"
  }
]
```

---

#### PUT /api/cuentas/{id}/activar
Activar/desactivar cuenta (Admin).

**Response (200):**
```json
{
  "message": "Cuenta activada exitosamente"
}
```

---

#### PUT /api/cuentas/cambiar-password
Cambiar contraseña propia.

**Request:**
```json
{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!"
}
```

**Response (200):**
```json
{
  "message": "Contraseña actualizada exitosamente"
}
```

## 🗄️ Modelo de Datos

### CuentaAuth

```java
@Entity
@Table(name = "cuentas_auth")
public class CuentaAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password; // Encriptado con BCrypt
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "cuenta_roles",
        joinColumns = @JoinColumn(name = "cuenta_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();
    
    @OneToOne(mappedBy = "cuentaAuth")
    private PerfilUsuario perfilUsuario;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
}
```

### Rol

```java
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RolNombre nombre; // ADMIN, USER
    
    private String descripcion;
}
```

## 🔐 Seguridad

### Encriptación de Contraseñas

```java
@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void registerUser(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        // Guardar con contraseña encriptada
    }
}
```

**Algoritmo:** BCrypt con 10 rondas

### JWT Configuration

**application.properties:**
```properties
jwt.secret=${JWT_SECRET:default-secret-key-change-in-production}
jwt.expiration=86400000  # 24 horas en milisegundos
jwt.issuer=nutritrack-api
```

**Estructura del Token:**
```json
{
  "sub": "usuario@example.com",
  "userId": 1,
  "roles": ["USER"],
  "iat": 1698753600,
  "exp": 1698840000,
  "iss": "nutritrack-api"
}
```

### JwtUtil - Métodos Principales

```java
public class JwtUtil {
    // Generar token
    public String generateToken(UserDetails userDetails);
    
    // Validar token
    public boolean validateToken(String token, UserDetails userDetails);
    
    // Extraer email
    public String extractUsername(String token);
    
    // Verificar expiración
    public boolean isTokenExpired(String token);
    
    // Extraer claims
    public Claims extractAllClaims(String token);
}
```

### Filtro de Autenticación

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && jwtUtil.validateToken(token)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(
                jwtUtil.extractUsername(token)
            );
            
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## 🛡️ Autorización

### Configuración de Seguridad

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

### Roles Disponibles

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **USER** | Usuario estándar | Acceso a sus propios datos |
| **ADMIN** | Administrador | Acceso completo al sistema |
| **NUTRITIONIST** | Nutricionista | Gestión de planes nutricionales |

### Anotaciones de Seguridad

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }

@PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
public void createPlan() { }

@PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
public void updateProfile(Long userId) { }
```

## 🧪 Testing

### Test de Registro

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test123!");
        request.setConfirmPassword("Test123!");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists());
    }
    
    @Test
    void testRegister_DuplicateEmail() throws Exception {
        // Test con email duplicado
        // Debe retornar 409 Conflict
    }
}
```

### Test de Login

```java
@Test
void testLogin_Success() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("Test123!");
    
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.type").value("Bearer"));
}
```

### Test de JWT

```java
@Test
void testJwtValidation() {
    UserDetails userDetails = createMockUserDetails();
    String token = jwtUtil.generateToken(userDetails);
    
    assertTrue(jwtUtil.validateToken(token, userDetails));
    assertEquals("test@example.com", jwtUtil.extractUsername(token));
    assertFalse(jwtUtil.isTokenExpired(token));
}
```

## 🚀 Despliegue

### Variables de Entorno Requeridas

```bash
# Producción
JWT_SECRET=tu-clave-super-secreta-de-al-menos-256-bits
JWT_EXPIRATION=86400000

# Base de datos
DB_URL=jdbc:mysql://localhost:3306/nutritrack_db
DB_USERNAME=nutritrack_user
DB_PASSWORD=secure_password
```

### Consideraciones de Seguridad

✅ **Mejores Prácticas:**
- Usar HTTPS en producción
- Almacenar JWT_SECRET en variables de entorno
- Implementar rate limiting en login
- Logs de intentos fallidos
- Bloqueo temporal tras múltiples fallos
- Validar tokens en blacklist (logout)

❌ **Evitar:**
- Hardcodear secretos
- Tokens sin expiración
- Almacenar contraseñas en texto plano
- Exponer stack traces en producción

## 📊 Métricas y Monitoreo

### Logs Importantes

```java
log.info("Usuario registrado: {}", email);
log.warn("Intento de login fallido para: {}", email);
log.error("Error generando token JWT", exception);
```

### Eventos a Monitorear

- Registros por día
- Logins exitosos/fallidos
- Tokens generados
- Intentos de acceso no autorizado
- Cambios de contraseña

## 🔄 Flujo de Implementación

### Paso 1: Configurar Base de Datos
```sql
CREATE TABLE cuentas_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE cuenta_roles (
    cuenta_id BIGINT,
    rol_id BIGINT,
    PRIMARY KEY (cuenta_id, rol_id),
    FOREIGN KEY (cuenta_id) REFERENCES cuentas_auth(id),
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);
```

### Paso 2: Implementar Entidades
Ver código en sección Modelo de Datos.

### Paso 3: Crear Repositorios
```java
public interface CuentaAuthRepository extends JpaRepository<CuentaAuth, Long> {
    Optional<CuentaAuth> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### Paso 4: Implementar Servicios
Ver AuthService y CuentaAuthService.

### Paso 5: Crear Controladores
Ver AuthController.

### Paso 6: Configurar Seguridad
Ver SecurityConfig.

### Paso 7: Testing
Implementar tests unitarios e integración.

## 📚 Referencias

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

---

**Contacto:** [Email del responsable]  
**Issues:** Reportar en GitHub con etiqueta `auth`
