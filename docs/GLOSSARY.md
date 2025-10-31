# Glosario - NutriTrack API 📖

Términos y conceptos importantes del proyecto.

## A

**API (Application Programming Interface)**  
Interfaz de programación que permite la comunicación entre diferentes sistemas de software.

**Actuator**  
Módulo de Spring Boot que proporciona endpoints de monitoreo y gestión.

**Auth (Authentication)**  
Proceso de verificar la identidad de un usuario.

## B

**Bean**  
Objeto gestionado por el contenedor de Spring.

**BCrypt**  
Algoritmo de hash utilizado para encriptar contraseñas.

**Builder Pattern**  
Patrón de diseño para construir objetos complejos paso a paso.

## C

**CORS (Cross-Origin Resource Sharing)**  
Mecanismo que permite que recursos restringidos en una página web sean solicitados desde otro dominio.

**Controller**  
Capa que maneja las peticiones HTTP y retorna respuestas.

**CRUD**  
Create, Read, Update, Delete - operaciones básicas de persistencia.

**CuentaAuth**  
Entidad que representa las credenciales de autenticación de un usuario.

## D

**DTO (Data Transfer Object)**  
Objeto usado para transferir datos entre capas del sistema.

**Dependency Injection**  
Patrón donde las dependencias son inyectadas en lugar de ser creadas internamente.

**Domain**  
El área de conocimiento o actividad del negocio que el software modela.

## E

**Entity**  
Clase Java que representa una tabla en la base de datos.

**Endpoint**  
URL específica donde un servicio web puede ser accedido.

**Etiqueta**  
Sistema de clasificación para categorizar ingredientes, ejercicios, metas y planes.

## F

**Filter**  
Componente que intercepta requests y responses para procesamiento adicional.

**Flyway**  
Herramienta de versionado y migración de bases de datos.

## H

**Health Check**  
Endpoint que indica el estado de salud de la aplicación.

**Hibernate**  
Framework de mapeo objeto-relacional (ORM) usado por JPA.

**HikariCP**  
Pool de conexiones de base de datos de alto rendimiento.

## I

**IMC (Índice de Masa Corporal)**  
Medida que relaciona peso y altura para evaluar el peso corporal.

**IoC (Inversion of Control)**  
Principio donde el control del flujo del programa es invertido.

## J

**JPA (Java Persistence API)**  
Especificación para el manejo de datos relacionales en Java.

**JPQL (Java Persistence Query Language)**  
Lenguaje de consultas orientado a objetos para JPA.

**JWT (JSON Web Token)**  
Estándar para crear tokens de acceso que permiten la autenticación.

**Jackson**  
Librería para serialización/deserialización de JSON.

## L

**Lombok**  
Librería que reduce código boilerplate mediante anotaciones.

**Layer (Capa)**  
Nivel de abstracción en la arquitectura (Controller, Service, Repository).

## M

**Maven**  
Herramienta de gestión de dependencias y construcción de proyectos.

**Migration**  
Script que modifica la estructura de la base de datos.

**MockMvc**  
Framework para testing de controladores Spring MVC.

## P

**PerfilUsuario**  
Entidad que almacena información personal del usuario.

**Payload**  
Datos útiles transmitidos en una petición o respuesta.

**Projection**  
Consulta que retorna solo un subconjunto de campos de una entidad.

## R

**Repository**  
Capa que abstrae el acceso a datos.

**REST (Representational State Transfer)**  
Estilo arquitectónico para servicios web.

**Rol**  
Conjunto de permisos asignados a un usuario.

**Rollback**  
Revertir cambios a un estado anterior.

## S

**Service**  
Capa que contiene la lógica de negocio.

**Spring Boot**  
Framework que simplifica el desarrollo de aplicaciones Spring.

**Spring Security**  
Framework para autenticación y autorización.

**Soft Delete**  
Marcar un registro como eliminado sin borrarlo físicamente.

**Specification**  
Patrón para crear consultas dinámicas con JPA.

## T

**Transaction**  
Conjunto de operaciones que se ejecutan como una unidad atómica.

**DTO Transformation**  
Conversión entre DTOs y entidades del dominio.

## U

**UsuarioHistorialMedida**  
Registro histórico de medidas corporales del usuario.

**UsuarioPerfilSalud**  
Información relacionada con objetivos y salud del usuario.

## V

**Validation**  
Proceso de verificar que los datos cumplan con reglas específicas.

**VO (Value Object)**  
Objeto inmutable que representa un valor sin identidad propia.

## W

**Wrapper**  
Clase que envuelve otra clase o tipo primitivo.

---

## Acrónimos Comunes

| Acrónimo | Significado |
|----------|-------------|
| API | Application Programming Interface |
| CRUD | Create, Read, Update, Delete |
| DTO | Data Transfer Object |
| HTTP | HyperText Transfer Protocol |
| JPA | Java Persistence API |
| JSON | JavaScript Object Notation |
| JWT | JSON Web Token |
| ORM | Object-Relational Mapping |
| REST | Representational State Transfer |
| SQL | Structured Query Language |
| URL | Uniform Resource Locator |

## Códigos de Estado HTTP

| Código | Significado | Uso en NutriTrack |
|--------|-------------|-------------------|
| 200 | OK | Operación exitosa |
| 201 | Created | Recurso creado |
| 204 | No Content | Eliminación exitosa |
| 400 | Bad Request | Datos inválidos |
| 401 | Unauthorized | No autenticado |
| 403 | Forbidden | Sin permisos |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Recurso duplicado |
| 500 | Internal Server Error | Error del servidor |

---

**Nota:** Este glosario se actualiza conforme el proyecto evoluciona.
