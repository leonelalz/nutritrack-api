package com.example.nutritrackapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI nutriTrackOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl("https://nutritrack-api-wt8b.onrender.com");
        prodServer.setDescription("Server URL in Production environment (Render)");

        Contact contact = new Contact();
        contact.setName("NutriTrack Team");
        contact.setEmail("support@nutritrack.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("NutriTrack API")
                .version("1.0.0")
                .contact(contact)
                .description("""
                    API REST para gestión de nutrición, salud y ejercicio.
                    
                    ## 🔐 Autenticación
                    Esta API utiliza JWT (JSON Web Tokens) para autenticación.
                    
                    **Pasos para autenticarse:**
                    1. Registra un usuario con POST /api/v1/auth/register o usa las cuentas de prueba
                    2. Inicia sesión con POST /api/v1/auth/login
                    3. Copia el token JWT de la respuesta
                    4. Haz clic en el botón "Authorize" 🔓 arriba
                    5. Pega el token en el campo "Value" (sin agregar "Bearer")
                    6. ¡Listo! Ahora puedes usar todos los endpoints protegidos
                    
                    El token expira en 24 horas.
                    
                    ## 👥 Usuarios de Prueba
                    
                    ### 🔹 Usuario Admin (Administrador)
                    - **Email:** admin@nutritrack.com
                    - **Password:** Admin123!
                    - **Role:** ROLE_ADMIN
                    - **Objetivo:** Mantener forma física
                    - **Actividad:** Alto (5-6 días/semana)
                    - **Peso:** 70.0 kg → 70.5 kg (11 mediciones desde sep-2025)
                    - **Altura:** 175 cm | **IMC:** 23.0 (Peso normal)
                    
                    ### 🔸 Usuario Demo (Usuario Regular)
                    - **Email:** demo@nutritrack.com
                    - **Password:** Demo123!
                    - **Role:** ROLE_USER
                    - **Objetivo:** Perder peso
                    - **Actividad:** Moderado (3-4 días/semana)
                    - **Peso:** 78.0 kg → 72.5 kg (-5.5 kg) 🎯
                    - **Altura:** 168 cm | **IMC:** 27.6 → 25.7 (Sobrepeso leve)
                    
                    ## 📋 Módulos Disponibles
                    
                    ### ✅ Módulo 1: Autenticación y Seguridad
                    - US-01: Registro de usuarios
                    - US-02: Inicio de sesión con JWT
                    - US-05: Eliminación de cuenta (requiere confirmación "ELIMINAR")
                    - Control de acceso basado en roles (ADMIN/USER)
                    
                    ### ✅ Módulo 2: Catálogo de Alimentos y Ejercicios
                    - Gestión de ingredientes, comidas y ejercicios
                    - Sistema de etiquetas para categorización
                    - Solo administradores pueden crear/modificar
                    - Usuarios pueden consultar el catálogo
                    
                    ### ✅ Módulo 3: Planes y Rutinas
                    - US-07: Consultar planes nutricionales disponibles
                    - US-08: Ver detalle de plan (comidas por día)
                    - US-09: Consultar rutinas de ejercicio disponibles
                    - US-10: Ver detalle de rutina (ejercicios por semana)
                    - Administradores gestionan planes y rutinas
                    
                    ### ✅ Módulo 4: Asignación de Planes y Rutinas
                    - US-18: Asignar plan nutricional a usuario
                    - US-19: Asignar rutina de ejercicio a usuario
                    - US-20: Gestión de estados (ACTIVO, PAUSADO, COMPLETADO, CANCELADO)
                    - Seguimiento de progreso y días completados
                    
                    ### ✅ Módulo 5: Tracking de Actividades Diarias
                    - US-21: Ver actividades programadas del día (plan y rutina)
                    - US-22: Registrar comidas y ejercicios realizados
                    - US-23: Eliminar registros (desmarcar actividades)
                    - Historial de actividades con rangos de fechas
                    - Cálculo automático de calorías consumidas/quemadas
                    
                    ## 🎯 Objetivos de Salud Disponibles
                    - **PERDER_PESO** - Pérdida de peso
                    - **GANAR_MASA_MUSCULAR** - Ganancia muscular
                    - **MANTENER_FORMA** - Mantenimiento físico
                    - **REHABILITACION** - Rehabilitación física
                    - **CONTROLAR_ESTRES** - Control de estrés
                    
                    ## 🏃 Niveles de Actividad Física
                    - **BAJO** - Ejercicio 1-2 días/semana o sedentario
                    - **MODERADO** - Ejercicio 3-4 días/semana
                    - **ALTO** - Ejercicio 5-6 días/semana o más
                    
                    ---
                    
                    **Desarrollado por:** Fabián Rojas  
                    **Asignatura:** Programación Móvil Transversal  
                    **Institución:** DUOC UC  
                    **Fecha:** Noviembre 2025
                    """)
                .license(mitLicense);

        // Configuración de seguridad JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingresa el token JWT obtenido del login (sin prefijo 'Bearer ')");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme));
    }
}
