# 🔐 SECURITY CONFIG - CONFIGURACIÓN COMPLETA DE SEGURIDAD

> **Documento de Referencia para Reconstrucción**  
> Contiene configuración completa de JWT + Spring Security + autenticación/autorización.  
> Compatible con Spring Boot 3.5.7 + Spring Security 6.x

---

## 📋 ÍNDICE DE CONTENIDOS

### CONFIGURACIÓN BASE
1. [Dependencias](#dependencias)
2. [application.properties](#applicationproperties)

### COMPONENTES PRINCIPALES
3. [JwtTokenProvider](#jwttokenprovider)
4. [JwtAuthenticationFilter](#jwtauthenticationfilter)
5. [CustomUserDetailsService](#customuserdetailsservice)
6. [SecurityConfig](#securityconfig)

### MODELOS Y EXCEPCIONES
7. [UserPrincipal](#userprincipal)
8. [Exception Handling](#exception-handling)

### TESTING
9. [Security Tests](#security-tests)

---

## ⚙️ DEPENDENCIAS

### pom.xml

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

---

## 📝 APPLICATION.PROPERTIES

### application.properties

```properties
# JWT Configuration
jwt.secret=${JWT_SECRET:nutritrack-jwt-secret-key-super-secure-2024-minimum-256-bits-required-for-hs256-algorithm}
jwt.expiration=86400000
# 86400000 ms = 24 horas

# Security
spring.security.user.name=admin
spring.security.user.password=admin
```

### application-prod.properties

```properties
# JWT Configuration - Production
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
# 3600000 ms = 1 hora (más seguro en producción)

# CORS - Production
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:https://nutritrack.app}
```

---

## 🔑 JWT TOKEN PROVIDER

### JwtTokenProvider.java

```java
package com.nutritrack.nutritrackapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Proveedor de tokens JWT para autenticación.
 * Genera, valida y extrae información de tokens JWT.
 */
@Slf4j
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    /**
     * Genera un token JWT a partir de la autenticación.
     * @param authentication Información de autenticación
     * @return Token JWT firmado
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        // Obtener roles del usuario
        String roles = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
            .subject(Long.toString(userPrincipal.getId()))
            .claim("email", userPrincipal.getEmail())
            .claim("roles", roles)
            .claim("perfilId", userPrincipal.getPerfilId())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }
    
    /**
     * Obtiene el ID del usuario desde el token.
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * Obtiene el email desde el token.
     * @param token Token JWT
     * @return Email del usuario
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }
    
    /**
     * Obtiene los claims del token.
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    /**
     * Valida la firma y expiración del token.
     * @param token Token JWT
     * @return true si es válido
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            
            return true;
        } catch (SignatureException ex) {
            log.error("Firma JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Token JWT malformado: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Claims JWT vacíos: {}", ex.getMessage());
        }
        
        return false;
    }
}
```

---

## 🛡️ JWT AUTHENTICATION FILTER

### JwtAuthenticationFilter.java

```java
package com.nutritrack.nutritrackapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta todas las requests para validar el token JWT.
 * Extiende OncePerRequestFilter para garantizar una sola ejecución por request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Usuario autenticado: {}", userId);
            }
        } catch (Exception ex) {
            log.error("No se pudo establecer la autenticación del usuario: {}", ex.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrae el token JWT del header Authorization.
     * @param request HTTP request
     * @return Token JWT sin el prefijo "Bearer "
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

---

## 👤 CUSTOM USER DETAILS SERVICE

### CustomUserDetailsService.java

```java
package com.nutritrack.nutritrackapi.security;

import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para cargar detalles del usuario durante la autenticación.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final CuentaAuthRepository cuentaAuthRepository;
    
    /**
     * Carga el usuario por email (username).
     * @param email Email del usuario
     * @return UserDetails con información del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CuentaAuth cuenta = cuentaAuthRepository.findByEmailWithProfile(email)
            .orElseThrow(() -> 
                new UsernameNotFoundException("Usuario no encontrado con email: " + email)
            );
        
        return UserPrincipal.create(cuenta);
    }
    
    /**
     * Carga el usuario por ID.
     * @param id ID del usuario
     * @return UserDetails con información del usuario
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        CuentaAuth cuenta = cuentaAuthRepository.findByIdWithProfile(id)
            .orElseThrow(() -> 
                new ResourceNotFoundException("Usuario no encontrado con id: " + id)
            );
        
        return UserPrincipal.create(cuenta);
    }
}
```

---

## 🧑‍💼 USER PRINCIPAL

### UserPrincipal.java

```java
package com.nutritrack.nutritrackapi.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutritrack.nutritrackapi.model.CuentaAuth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Representa el principal autenticado en el contexto de seguridad.
 * Implementa UserDetails para integrarse con Spring Security.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String email;
    
    @JsonIgnore
    private String password;
    
    private String rol;
    private Long perfilId;
    private String nombreCompleto;
    
    private Collection<? extends GrantedAuthority> authorities;
    
    /**
     * Crea un UserPrincipal desde una CuentaAuth.
     * @param cuenta Cuenta de autenticación
     * @return UserPrincipal
     */
    public static UserPrincipal create(CuentaAuth cuenta) {
        Collection<GrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + cuenta.getRol().getTipo().name())
        );
        
        String nombreCompleto = cuenta.getPerfilUsuario() != null
            ? cuenta.getPerfilUsuario().getNombre() + " " + cuenta.getPerfilUsuario().getApellido()
            : null;
        
        Long perfilId = cuenta.getPerfilUsuario() != null
            ? cuenta.getPerfilUsuario().getId()
            : null;
        
        return new UserPrincipal(
            cuenta.getId(),
            cuenta.getEmail(),
            cuenta.getPassword(),
            cuenta.getRol().getTipo().name(),
            perfilId,
            nombreCompleto,
            authorities
        );
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

---

## 🔒 SECURITY CONFIG

### SecurityConfig.java

```java
package com.nutritrack.nutritrackapi.config;

import com.nutritrack.nutritrackapi.security.CustomUserDetailsService;
import com.nutritrack.nutritrackapi.security.JwtAuthenticationEntryPoint;
import com.nutritrack.nutritrackapi.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad de la aplicación.
 * Define la cadena de filtros, autenticación y autorización.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    
    /**
     * Cadena de filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler)
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Swagger/OpenAPI
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                
                // H2 Console (solo en desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Endpoints protegidos por rol
                .requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasRole("ADMIN")
                
                .requestMatchers("/api/nutricionista/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers("/api/entrenador/**").hasAnyRole("ENTRENADOR", "ADMIN")
                
                // Módulo 1: Perfiles
                .requestMatchers("/api/perfil/**").authenticated()
                
                // Módulo 2: Catálogo (lectura pública, escritura admin)
                .requestMatchers(HttpMethod.GET, "/api/ingredientes/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ingredientes/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/ingredientes/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/ingredientes/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET, "/api/ejercicios/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/ejercicios/**").hasAnyRole("ENTRENADOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/ejercicios/**").hasAnyRole("ENTRENADOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/ejercicios/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET, "/api/comidas/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/comidas/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/comidas/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/comidas/**").hasRole("ADMIN")
                
                // Módulo 3: Planes y Rutinas
                .requestMatchers(HttpMethod.GET, "/api/planes/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/planes/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/planes/**").hasAnyRole("NUTRICIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/planes/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET, "/api/rutinas/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/rutinas/**").hasAnyRole("ENTRENADOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/rutinas/**").hasAnyRole("ENTRENADOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/rutinas/**").hasRole("ADMIN")
                
                // Módulo 4: Asignaciones
                .requestMatchers("/api/usuario-planes/**").authenticated()
                .requestMatchers("/api/usuario-rutinas/**").authenticated()
                
                // Módulo 5: Seguimiento
                .requestMatchers("/api/registros-comida/**").authenticated()
                .requestMatchers("/api/registros-ejercicio/**").authenticated()
                
                // Cualquier otra request requiere autenticación
                .anyRequest().authenticated()
            );
        
        // Agregar filtro JWT antes del filtro de autenticación
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Permitir frames para H2 Console (solo en desarrollo)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        
        return http.build();
    }
    
    /**
     * Configuración de CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200",
            "http://localhost:8080"
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Proveedor de autenticación DAO.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }
    
    /**
     * AuthenticationManager para autenticación manual.
     */
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * Encoder de contraseñas BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## ⚠️ EXCEPTION HANDLING

### JwtAuthenticationEntryPoint.java

```java
package com.nutritrack.nutritrackapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja las excepciones de autenticación.
 * Retorna un JSON con error 401 cuando el usuario no está autenticado.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        
        log.error("Error de autenticación: {}", authException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Credenciales inválidas o token expirado");
        body.put("path", request.getServletPath());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
```

---

## 🧪 SECURITY TESTS

### JwtTokenProviderTest.java

```java
package com.nutritrack.nutritrackapi.security;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.TipoRol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JwtTokenProvider - Tests Unitarios")
class JwtTokenProviderTest {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    private Authentication authentication;
    
    @BeforeEach
    void setUp() {
        Rol rol = Rol.builder()
            .id(1L)
            .tipo(TipoRol.USUARIO)
            .build();
        
        CuentaAuth cuenta = CuentaAuth.builder()
            .id(1L)
            .email("test@example.com")
            .password("password")
            .rol(rol)
            .build();
        
        UserPrincipal userPrincipal = UserPrincipal.create(cuenta);
        
        authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities()
        );
    }
    
    @Test
    @DisplayName("Generar token JWT exitoso")
    void testGenerateToken() {
        // Act
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // Header.Payload.Signature
    }
    
    @Test
    @DisplayName("Validar token JWT válido")
    void testValidateToken_Valid() {
        // Arrange
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        // Assert
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Validar token JWT inválido")
    void testValidateToken_Invalid() {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        
        // Assert
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Extraer user ID del token")
    void testGetUserIdFromToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Act
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        // Assert
        assertThat(userId).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Extraer email del token")
    void testGetEmailFromToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Act
        String email = jwtTokenProvider.getEmailFromToken(token);
        
        // Assert
        assertThat(email).isEqualTo("test@example.com");
    }
}
```

---

## 📚 ANOTACIONES DE SEGURIDAD

### Uso en Controllers

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // Solo ADMIN puede acceder
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ingredientes/{id}")
    public ResponseEntity<?> eliminarIngrediente(@PathVariable Long id) {
        // ...
    }
    
    // NUTRICIONISTA o ADMIN pueden acceder
    @PreAuthorize("hasAnyRole('NUTRICIONISTA', 'ADMIN')")
    @PostMapping("/planes")
    public ResponseEntity<?> crearPlan(@RequestBody CrearPlanRequest request) {
        // ...
    }
    
    // Validar que el usuario solo acceda a sus propios recursos
    @PreAuthorize("#perfilId == authentication.principal.perfilId or hasRole('ADMIN')")
    @GetMapping("/perfil/{perfilId}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long perfilId) {
        // ...
    }
}
```

### Obtener usuario autenticado

```java
@RestController
public class MiController {
    
    @GetMapping("/mi-perfil")
    public ResponseEntity<?> getMiPerfil(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long perfilId = userPrincipal.getPerfilId();
        // ...
    }
    
    // Alternativa usando SecurityContext
    public void otroMetodo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Long userId = userPrincipal.getId();
    }
}
```

---

## 🔐 ROLES Y PERMISOS

### Jerarquía de Roles

| Rol           | Descripción                        | Permisos                                    |
|---------------|------------------------------------|--------------------------------------------|
| USUARIO       | Usuario estándar                   | Ver catálogo, registrar comidas/ejercicios |
| NUTRICIONISTA | Profesional de nutrición           | USUARIO + crear/editar planes y comidas    |
| ENTRENADOR    | Profesional de ejercicio           | USUARIO + crear/editar rutinas y ejercicios|
| ADMIN         | Administrador del sistema          | Todos los permisos                         |

---

**Documento completado:** Configuración completa de JWT + Spring Security con autenticación y autorización basada en roles.
