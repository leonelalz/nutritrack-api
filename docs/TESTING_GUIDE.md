# 🧪 TESTING GUIDE - GUÍA COMPLETA DE PRUEBAS

> **Documento de Referencia para Reconstrucción**  
> Contiene ejemplos completos de tests unitarios y de integración.  
> Compatible con Spring Boot 3.5.7 + JUnit 5 + Mockito + Testcontainers

---

## 📋 ÍNDICE DE CONTENIDOS

### CONFIGURACIÓN BASE
1. [Dependencias y Setup](#dependencias-y-setup)
2. [Estructura de Carpetas](#estructura-de-carpetas)
3. [Configuración de Testing](#configuración-de-testing)

### TESTS UNITARIOS
4. [Service Tests](#service-tests)
    - AuthService
    - PerfilUsuarioService
    - IngredienteService
    - PlanService
    - UsuarioPlanService
5. [Repository Tests](#repository-tests)
6. [Mapper Tests](#mapper-tests)

### TESTS DE INTEGRACIÓN
7. [Controller Integration Tests](#controller-integration-tests)
8. [Database Integration Tests](#database-integration-tests)

### BUENAS PRÁCTICAS
9. [Patterns y Antipatrones](#patterns-y-antipatrones)
10. [Cobertura de Código](#cobertura-de-código)

---

## ⚙️ DEPENDENCIAS Y SETUP

### pom.xml

```xml
<!-- Testing Dependencies -->
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2 Database for testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito (included in spring-boot-starter-test) -->
    <!-- AssertJ (included in spring-boot-starter-test) -->
    
    <!-- Optional: Testcontainers for PostgreSQL tests -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<!-- Surefire Plugin for tests -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
            </configuration>
        </plugin>
        
        <!-- JaCoCo for code coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.10</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## 📁 ESTRUCTURA DE CARPETAS

```
src/test/java/com/nutritrack/nutritrackapi/
├── unit/                         # Tests unitarios
│   ├── service/
│   │   ├── AuthServiceTest.java
│   │   ├── PerfilUsuarioServiceTest.java
│   │   ├── IngredienteServiceTest.java
│   │   ├── ComidaServiceTest.java
│   │   ├── PlanServiceTest.java
│   │   └── UsuarioPlanServiceTest.java
│   ├── mapper/
│   │   ├── IngredienteMapperTest.java
│   │   └── PlanMapperTest.java
│   └── util/
│       └── JwtUtilsTest.java
├── integration/                  # Tests de integración
│   ├── controller/
│   │   ├── AuthControllerIntegrationTest.java
│   │   ├── IngredienteControllerIntegrationTest.java
│   │   └── PlanControllerIntegrationTest.java
│   └── repository/
│       ├── IngredienteRepositoryTest.java
│       └── PlanRepositoryTest.java
└── NutritrackApiApplicationTests.java

src/test/resources/
├── application-test.properties   # Configuración para tests
└── data.sql                      # Datos de prueba opcionales
```

---

## 🔧 CONFIGURACIÓN DE TESTING

### application-test.properties

```properties
# H2 Database for testing
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Disable banner
spring.main.banner-mode=off

# Logging
logging.level.com.nutritrack=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# JWT Configuration (test keys)
jwt.secret=testSecretKeyForJWTTokenGeneration1234567890
jwt.expiration=3600000
```

---

## 🧪 SERVICE TESTS (UNITARIOS)

### AuthServiceTest

```java
package com.nutritrack.nutritrackapi.unit.service;

import com.nutritrack.nutritrackapi.dto.auth.LoginRequest;
import com.nutritrack.nutritrackapi.dto.auth.RegisterRequest;
import com.nutritrack.nutritrackapi.dto.auth.JwtResponse;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.RolRepository;
import com.nutritrack.nutritrackapi.security.JwtTokenProvider;
import com.nutritrack.nutritrackapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Tests Unitarios")
class AuthServiceTest {
    
    @Mock
    private CuentaAuthRepository cuentaAuthRepository;
    
    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;
    
    @Mock
    private RolRepository rolRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Rol rolUsuario;
    private CuentaAuth cuentaAuth;
    private PerfilUsuario perfilUsuario;
    
    @BeforeEach
    void setUp() {
        // Arrange - Datos de prueba
        registerRequest = RegisterRequest.builder()
            .email("test@example.com")
            .password("Password123")
            .nombre("Test")
            .apellido("User")
            .unidadesMedida("KG")
            .build();
        
        loginRequest = LoginRequest.builder()
            .email("test@example.com")
            .password("Password123")
            .build();
        
        rolUsuario = Rol.builder()
            .id(1L)
            .tipo(TipoRol.USUARIO)
            .descripcion("Usuario estándar")
            .build();
        
        perfilUsuario = PerfilUsuario.builder()
            .id(1L)
            .nombre("Test")
            .apellido("User")
            .unidadesMedida(UnidadesMedida.KG)
            .build();
        
        cuentaAuth = CuentaAuth.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .rol(rolUsuario)
            .perfilUsuario(perfilUsuario)
            .build();
    }
    
    @Test
    @DisplayName("Registro de usuario exitoso")
    void testRegistrar_Success() {
        // Arrange
        when(cuentaAuthRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findByTipo(TipoRol.USUARIO)).thenReturn(Optional.of(rolUsuario));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(cuentaAuthRepository.save(any(CuentaAuth.class))).thenReturn(cuentaAuth);
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfilUsuario);
        
        // Act
        CuentaAuth resultado = authService.registrar(registerRequest);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("test@example.com");
        assertThat(resultado.getRol().getTipo()).isEqualTo(TipoRol.USUARIO);
        assertThat(resultado.getPerfilUsuario()).isNotNull();
        
        verify(cuentaAuthRepository).existsByEmail("test@example.com");
        verify(rolRepository).findByTipo(TipoRol.USUARIO);
        verify(passwordEncoder).encode("Password123");
        verify(cuentaAuthRepository).save(any(CuentaAuth.class));
        verify(perfilUsuarioRepository).save(any(PerfilUsuario.class));
    }
    
    @Test
    @DisplayName("Registro falla si el email ya existe")
    void testRegistrar_EmailExists() {
        // Arrange
        when(cuentaAuthRepository.existsByEmail(anyString())).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> authService.registrar(registerRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El email ya está registrado");
        
        verify(cuentaAuthRepository).existsByEmail("test@example.com");
        verify(cuentaAuthRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Login exitoso genera JWT token")
    void testLogin_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(cuentaAuthRepository.findByEmailWithProfile(anyString()))
            .thenReturn(Optional.of(cuentaAuth));
        when(jwtTokenProvider.generateToken(any(Authentication.class)))
            .thenReturn("jwt.token.here");
        
        // Act
        JwtResponse response = authService.login(loginRequest);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.token.here");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRol()).isEqualTo("USUARIO");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(cuentaAuthRepository).findByEmailWithProfile("test@example.com");
        verify(jwtTokenProvider).generateToken(authentication);
    }
    
    @Test
    @DisplayName("Login falla si el usuario no existe")
    void testLogin_UserNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(cuentaAuthRepository.findByEmailWithProfile(anyString()))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Usuario no encontrado");
        
        verify(cuentaAuthRepository).findByEmailWithProfile("test@example.com");
        verify(jwtTokenProvider, never()).generateToken(any());
    }
}
```

---

### PerfilUsuarioServiceTest

```java
package com.nutritrack.nutritrackapi.unit.service;

import com.nutritrack.nutritrackapi.dto.perfil.ActualizarPerfilRequest;
import com.nutritrack.nutritrackapi.dto.perfil.RegistrarMedidaRequest;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioHistorialMedidaRepository;
import com.nutritrack.nutritrackapi.service.PerfilUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerfilUsuarioService - Tests Unitarios")
class PerfilUsuarioServiceTest {
    
    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;
    
    @Mock
    private UsuarioHistorialMedidaRepository historialMedidaRepository;
    
    @InjectMocks
    private PerfilUsuarioService perfilUsuarioService;
    
    private PerfilUsuario perfilUsuario;
    private ActualizarPerfilRequest actualizarRequest;
    private RegistrarMedidaRequest medidaRequest;
    
    @BeforeEach
    void setUp() {
        perfilUsuario = PerfilUsuario.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .unidadesMedida(UnidadesMedida.KG)
            .fechaInicioApp(LocalDate.now())
            .build();
        
        actualizarRequest = ActualizarPerfilRequest.builder()
            .nombre("Juan Carlos")
            .apellido("Pérez García")
            .unidadesMedida("LBS")
            .build();
        
        medidaRequest = RegistrarMedidaRequest.builder()
            .peso(new BigDecimal("75.5"))
            .altura(new BigDecimal("175"))
            .build();
    }
    
    @Test
    @DisplayName("Obtener perfil por ID exitoso")
    void testObtenerPerfilPorId_Success() {
        // Arrange
        when(perfilUsuarioRepository.findByIdWithDetails(1L))
            .thenReturn(Optional.of(perfilUsuario));
        
        // Act
        PerfilUsuario resultado = perfilUsuarioService.obtenerPerfilPorId(1L);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        
        verify(perfilUsuarioRepository).findByIdWithDetails(1L);
    }
    
    @Test
    @DisplayName("Obtener perfil lanza excepción si no existe")
    void testObtenerPerfilPorId_NotFound() {
        // Arrange
        when(perfilUsuarioRepository.findByIdWithDetails(999L))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> perfilUsuarioService.obtenerPerfilPorId(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Perfil no encontrado");
        
        verify(perfilUsuarioRepository).findByIdWithDetails(999L);
    }
    
    @Test
    @DisplayName("Actualizar perfil exitoso")
    void testActualizarPerfil_Success() {
        // Arrange
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfilUsuario);
        
        // Act
        PerfilUsuario resultado = perfilUsuarioService.actualizarPerfil(1L, actualizarRequest);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Carlos");
        assertThat(resultado.getApellido()).isEqualTo("Pérez García");
        assertThat(resultado.getUnidadesMedida()).isEqualTo(UnidadesMedida.LBS);
        
        verify(perfilUsuarioRepository).findById(1L);
        verify(perfilUsuarioRepository).save(perfilUsuario);
    }
    
    @Test
    @DisplayName("Registrar medida calcula IMC correctamente")
    void testRegistrarMedida_CalculatesIMC() {
        // Arrange
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
        
        ArgumentCaptor<UsuarioHistorialMedida> medidaCaptor = 
            ArgumentCaptor.forClass(UsuarioHistorialMedida.class);
        
        // Act
        perfilUsuarioService.registrarMedida(1L, medidaRequest);
        
        // Assert
        verify(historialMedidaRepository).save(medidaCaptor.capture());
        
        UsuarioHistorialMedida medidaGuardada = medidaCaptor.getValue();
        assertThat(medidaGuardada.getPeso()).isEqualByComparingTo(new BigDecimal("75.5"));
        assertThat(medidaGuardada.getAltura()).isEqualByComparingTo(new BigDecimal("175"));
        assertThat(medidaGuardada.getImc()).isNotNull();
        assertThat(medidaGuardada.getImc()).isGreaterThan(BigDecimal.ZERO);
        assertThat(medidaGuardada.getFechaMedicion()).isEqualTo(LocalDate.now());
    }
    
    @Test
    @DisplayName("Validar rango de peso")
    void testRegistrarMedida_ValidatePesoRange() {
        // Arrange - Peso inválido
        RegistrarMedidaRequest invalidRequest = RegistrarMedidaRequest.builder()
            .peso(new BigDecimal("10")) // Muy bajo
            .altura(new BigDecimal("175"))
            .build();
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
        
        // Act & Assert
        assertThatThrownBy(() -> perfilUsuarioService.registrarMedida(1L, invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El peso debe estar entre 20 y 600 kg");
    }
}
```

---

### IngredienteServiceTest

```java
package com.nutritrack.nutritrackapi.unit.service;

import com.nutritrack.nutritrackapi.dto.ingrediente.CrearIngredienteRequest;
import com.nutritrack.nutritrackapi.exception.ResourceAlreadyExistsException;
import com.nutritrack.nutritrackapi.model.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import com.nutritrack.nutritrackapi.service.IngredienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngredienteService - Tests Unitarios")
class IngredienteServiceTest {
    
    @Mock
    private IngredienteRepository ingredienteRepository;
    
    @InjectMocks
    private IngredienteService ingredienteService;
    
    private CrearIngredienteRequest request;
    private Ingrediente ingrediente;
    
    @BeforeEach
    void setUp() {
        request = CrearIngredienteRequest.builder()
            .nombre("Pollo")
            .proteinas(new BigDecimal("27"))
            .carbohidratos(new BigDecimal("0"))
            .grasas(new BigDecimal("3.6"))
            .energia(new BigDecimal("165"))
            .grupoAlimenticio("PROTEINAS_ANIMALES")
            .build();
        
        ingrediente = Ingrediente.builder()
            .id(1L)
            .nombre("Pollo")
            .proteinas(new BigDecimal("27"))
            .carbohidratos(new BigDecimal("0"))
            .grasas(new BigDecimal("3.6"))
            .energia(new BigDecimal("165"))
            .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
            .build();
    }
    
    @Test
    @DisplayName("Crear ingrediente exitoso")
    void testCrearIngrediente_Success() {
        // Arrange
        when(ingredienteRepository.existsByNombre(anyString())).thenReturn(false);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);
        
        // Act
        Ingrediente resultado = ingredienteService.crear(request);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Pollo");
        assertThat(resultado.getProteinas()).isEqualByComparingTo(new BigDecimal("27"));
        
        verify(ingredienteRepository).existsByNombre("Pollo");
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }
    
    @Test
    @DisplayName("Crear ingrediente falla si ya existe (RN07)")
    void testCrearIngrediente_AlreadyExists() {
        // Arrange
        when(ingredienteRepository.existsByNombre(anyString())).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> ingredienteService.crear(request))
            .isInstanceOf(ResourceAlreadyExistsException.class)
            .hasMessageContaining("Ya existe un ingrediente con el nombre");
        
        verify(ingredienteRepository).existsByNombre("Pollo");
        verify(ingredienteRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Listar ingredientes con paginación")
    void testListarIngredientes_Paginated() {
        // Arrange
        List<Ingrediente> ingredientes = Arrays.asList(ingrediente);
        Page<Ingrediente> page = new PageImpl<>(ingredientes);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(ingredienteRepository.findAll(pageable)).thenReturn(page);
        
        // Act
        Page<Ingrediente> resultado = ingredienteService.listar(pageable);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNombre()).isEqualTo("Pollo");
        
        verify(ingredienteRepository).findAll(pageable);
    }
}
```

---

### PlanServiceTest

```java
package com.nutritrack.nutritrackapi.unit.service;

import com.nutritrack.nutritrackapi.dto.plan.AgregarComidaPlanRequest;
import com.nutritrack.nutritrackapi.dto.plan.CrearPlanRequest;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.ComidaRepository;
import com.nutritrack.nutritrackapi.repository.PlanDiaRepository;
import com.nutritrack.nutritrackapi.repository.PlanRepository;
import com.nutritrack.nutritrackapi.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlanService - Tests Unitarios")
class PlanServiceTest {
    
    @Mock
    private PlanRepository planRepository;
    
    @Mock
    private PlanDiaRepository planDiaRepository;
    
    @Mock
    private ComidaRepository comidaRepository;
    
    @InjectMocks
    private PlanService planService;
    
    private CrearPlanRequest crearPlanRequest;
    private Plan plan;
    private Comida comida;
    
    @BeforeEach
    void setUp() {
        crearPlanRequest = CrearPlanRequest.builder()
            .nombre("Plan Saludable")
            .descripcion("Plan para bajar de peso")
            .duracionDias(30)
            .build();
        
        plan = Plan.builder()
            .id(1L)
            .nombre("Plan Saludable")
            .descripcion("Plan para bajar de peso")
            .duracionDias(30)
            .activo(true)
            .build();
        
        comida = Comida.builder()
            .id(1L)
            .nombre("Ensalada de pollo")
            .tipoComida(TipoComida.ALMUERZO)
            .build();
    }
    
    @Test
    @DisplayName("Crear plan exitoso")
    void testCrearPlan_Success() {
        // Arrange
        when(planRepository.existsByNombre(anyString())).thenReturn(false);
        when(planRepository.save(any(Plan.class))).thenReturn(plan);
        
        // Act
        Plan resultado = planService.crear(crearPlanRequest);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Plan Saludable");
        assertThat(resultado.getDuracionDias()).isEqualTo(30);
        
        verify(planRepository).existsByNombre("Plan Saludable");
        verify(planRepository).save(any(Plan.class));
    }
    
    @Test
    @DisplayName("Agregar comida a plan - día válido")
    void testAgregarComida_Success() {
        // Arrange
        AgregarComidaPlanRequest request = AgregarComidaPlanRequest.builder()
            .numeroDia(1)
            .tipoComida("ALMUERZO")
            .comidaId(1L)
            .notas("Almuerzo ligero")
            .build();
        
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(planDiaRepository.existsByPlanIdAndNumeroDiaAndTipoComida(anyLong(), anyInt(), any()))
            .thenReturn(false);
        
        // Act
        planService.agregarComida(1L, request);
        
        // Assert
        verify(planDiaRepository).save(any(PlanDia.class));
    }
    
    @Test
    @DisplayName("Agregar comida a plan - día inválido")
    void testAgregarComida_InvalidDay() {
        // Arrange
        AgregarComidaPlanRequest request = AgregarComidaPlanRequest.builder()
            .numeroDia(50) // Fuera del rango (plan de 30 días)
            .tipoComida("ALMUERZO")
            .comidaId(1L)
            .build();
        
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        
        // Act & Assert
        assertThatThrownBy(() -> planService.agregarComida(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El día debe estar entre 1 y 30");
        
        verify(planDiaRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Eliminar plan con asignaciones activas falla (RN08)")
    void testEliminarPlan_WithActiveAssignments() {
        // Arrange
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.countAsignacionesActivasByPlanId(1L)).thenReturn(3L);
        
        // Act & Assert
        assertThatThrownBy(() -> planService.eliminar(1L))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("No se puede eliminar el plan porque tiene 3 asignaciones activas");
        
        verify(planRepository, never()).delete(any());
    }
}
```

---

## 🗂️ REPOSITORY TESTS

### IngredienteRepositoryTest

```java
package com.nutritrack.nutritrackapi.integration.repository;

import com.nutritrack.nutritrackapi.model.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("IngredienteRepository - Tests de Integración")
class IngredienteRepositoryTest {
    
    @Autowired
    private IngredienteRepository ingredienteRepository;
    
    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;
    
    @BeforeEach
    void setUp() {
        ingrediente1 = Ingrediente.builder()
            .nombre("Pollo")
            .proteinas(new BigDecimal("27"))
            .carbohidratos(new BigDecimal("0"))
            .grasas(new BigDecimal("3.6"))
            .energia(new BigDecimal("165"))
            .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
            .build();
        
        ingrediente2 = Ingrediente.builder()
            .nombre("Arroz")
            .proteinas(new BigDecimal("2.7"))
            .carbohidratos(new BigDecimal("28"))
            .grasas(new BigDecimal("0.3"))
            .energia(new BigDecimal("130"))
            .grupoAlimenticio(GrupoAlimenticio.CEREALES)
            .build();
        
        ingredienteRepository.save(ingrediente1);
        ingredienteRepository.save(ingrediente2);
    }
    
    @Test
    @DisplayName("findByNombre encuentra ingrediente por nombre exacto")
    void testFindByNombre() {
        // Act
        Optional<Ingrediente> resultado = ingredienteRepository.findByNombre("Pollo");
        
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Pollo");
    }
    
    @Test
    @DisplayName("findByNombreContainingIgnoreCase busca por texto parcial")
    void testFindByNombreContaining() {
        // Act
        Page<Ingrediente> resultado = ingredienteRepository
            .findByNombreContainingIgnoreCase("po", PageRequest.of(0, 10));
        
        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNombre()).isEqualTo("Pollo");
    }
    
    @Test
    @DisplayName("findByGrupoAlimenticio filtra por grupo")
    void testFindByGrupoAlimenticio() {
        // Act
        Page<Ingrediente> resultado = ingredienteRepository
            .findByGrupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES, PageRequest.of(0, 10));
        
        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNombre()).isEqualTo("Pollo");
    }
    
    @Test
    @DisplayName("existsByNombre verifica existencia")
    void testExistsByNombre() {
        // Act
        boolean existe = ingredienteRepository.existsByNombre("Pollo");
        boolean noExiste = ingredienteRepository.existsByNombre("Leche");
        
        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
```

---

## 🌐 CONTROLLER INTEGRATION TESTS

### AuthControllerIntegrationTest

```java
package com.nutritrack.nutritrackapi.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritrack.nutritrackapi.dto.auth.LoginRequest;
import com.nutritrack.nutritrackapi.dto.auth.RegisterRequest;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import com.nutritrack.nutritrackapi.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("AuthController - Tests de Integración")
class AuthControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CuentaAuthRepository cuentaAuthRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Rol rolUsuario;
    
    @BeforeEach
    void setUp() {
        rolUsuario = rolRepository.findByTipo(TipoRol.USUARIO)
            .orElseGet(() -> rolRepository.save(
                Rol.builder()
                    .tipo(TipoRol.USUARIO)
                    .descripcion("Usuario estándar")
                    .build()
            ));
    }
    
    @Test
    @DisplayName("POST /api/auth/register - Registro exitoso")
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
            .email("test@example.com")
            .password("Password123")
            .nombre("Test")
            .apellido("User")
            .unidadesMedida("KG")
            .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.rol.tipo").value("USUARIO"));
    }
    
    @Test
    @DisplayName("POST /api/auth/register - Validación de email")
    void testRegister_InvalidEmail() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
            .email("invalid-email")
            .password("Password123")
            .nombre("Test")
            .apellido("User")
            .unidadesMedida("KG")
            .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value(containsString("email válido")));
    }
    
    @Test
    @DisplayName("POST /api/auth/login - Login exitoso")
    void testLogin_Success() throws Exception {
        // Arrange - Crear usuario primero
        CuentaAuth cuenta = CuentaAuth.builder()
            .email("test@example.com")
            .password(passwordEncoder.encode("Password123"))
            .rol(rolUsuario)
            .build();
        cuentaAuthRepository.save(cuenta);
        
        LoginRequest request = LoginRequest.builder()
            .email("test@example.com")
            .password("Password123")
            .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

---

## 📊 PATTERNS Y BUENAS PRÁCTICAS

### 1. AAA Pattern (Arrange-Act-Assert)

```java
@Test
void testExample() {
    // Arrange - Preparar datos y mocks
    when(repository.findById(1L)).thenReturn(Optional.of(entity));
    
    // Act - Ejecutar el método a probar
    Entity result = service.getById(1L);
    
    // Assert - Verificar resultados
    assertThat(result).isNotNull();
    verify(repository).findById(1L);
}
```

### 2. Builder Pattern para Test Data

```java
@BeforeEach
void setUp() {
    // Usar builders para crear objetos de prueba
    ingrediente = Ingrediente.builder()
        .id(1L)
        .nombre("Pollo")
        .proteinas(new BigDecimal("27"))
        .build();
}
```

### 3. Test Fixtures (datos compartidos)

```java
class TestFixtures {
    public static Ingrediente crearIngredientePollo() {
        return Ingrediente.builder()
            .nombre("Pollo")
            .proteinas(new BigDecimal("27"))
            .build();
    }
}
```

### 4. Parametrized Tests

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "a", "ab"})
@DisplayName("Validar nombre con valores inválidos")
void testValidarNombre_Invalid(String nombre) {
    // Arrange
    CrearIngredienteRequest request = CrearIngredienteRequest.builder()
        .nombre(nombre)
        .build();
    
    // Act & Assert
    assertThatThrownBy(() -> service.crear(request))
        .isInstanceOf(ValidationException.class);
}
```

---

## 📈 COBERTURA DE CÓDIGO

### Ejecutar tests con cobertura

```powershell
# Ejecutar todos los tests
mvn clean test

# Ver reporte de cobertura
mvn jacoco:report

# El reporte se genera en: target/site/jacoco/index.html
```

### Metas de cobertura recomendadas

- **Services:** 80%+ cobertura
- **Controllers:** 70%+ cobertura
- **Repositories:** 60%+ (queries custom)
- **DTOs/Models:** No requieren tests directos

---

**Documento completado:** 30+ ejemplos de tests completos (unitarios + integración) con JUnit 5 + Mockito.
