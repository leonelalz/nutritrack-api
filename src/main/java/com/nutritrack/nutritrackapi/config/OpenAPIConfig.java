package com.nutritrack.nutritrackapi.config;

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
public class OpenAPIConfig {

    @Bean
    public OpenAPI nutriTrackOpenAPI() {
        // Servidor local de desarrollo
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de Desarrollo");

        // Información de contacto
        Contact contact = new Contact();
        contact.setEmail("nutritrack@upc.edu.pe");
        contact.setName("NutriTrack Team");
        contact.setUrl("https://github.com/leonelalz/nutritrack-api");

        // Licencia
        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        // Información general de la API
        Info info = new Info()
                .title("NutriTrack API")
                .version("0.1.0")
                .contact(contact)
                .description("API RESTful para gestión de planes nutricionales y seguimiento de ejercicios. " +
                        "Esta API permite a los usuarios registrarse, gestionar su perfil de salud, " +
                        "asignar metas nutricionales y de ejercicio, y hacer seguimiento de su progreso.")
                .termsOfService("https://nutritrack.com/terms")
                .license(mitLicense);

        // Esquema de seguridad JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT token de autenticación. Obtén el token mediante /api/v1/auth/login o /api/v1/auth/register");

        // Requisito de seguridad
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("bearerAuth", securityScheme);
    }
}
