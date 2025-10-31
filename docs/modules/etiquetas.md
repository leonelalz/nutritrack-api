# Módulo de Etiquetas 🏷️

**Responsable:** [Nombre del Desarrollador]  
**Última actualización:** Octubre 2025

## 📋 Descripción

Módulo encargado de gestionar el sistema de etiquetado para ingredientes, ejercicios, metas y planes nutricionales. Permite categorizar y buscar recursos mediante tags personalizables.

## 🎯 Responsabilidades

- CRUD de etiquetas generales
- Asignación de etiquetas a ingredientes
- Asignación de etiquetas a ejercicios
- Asignación de etiquetas a metas
- Asignación de etiquetas a planes
- Búsqueda y filtrado por etiquetas
- Gestión de categorías de etiquetas

## 📁 Estructura de Archivos

```
src/main/java/com/nutritrack/nutritrackapi/
├── controller/
│   └── EtiquetaController.java           # Endpoints de etiquetas
├── service/
│   └── EtiquetaService.java             # Lógica de negocio
├── repository/
│   ├── EtiquetaRepository.java          # CRUD de etiquetas
│   ├── EtiquetaIngredienteRepository.java
│   ├── EtiquetaEjercicioRepository.java
│   ├── EtiquetaMetaRepository.java
│   └── EtiquetaPlanRepository.java
├── model/
│   ├── Etiqueta.java                    # Entidad principal
│   ├── EtiquetaIngrediente.java         # Relación con ingredientes
│   ├── EtiquetaEjercicio.java           # Relación con ejercicios
│   ├── EtiquetaMeta.java                # Relación con metas
│   └── EtiquetaPlan.java                # Relación con planes
└── dto/
    ├── request/
    │   ├── EtiquetaRequest.java
    │   └── AsignarEtiquetasRequest.java
    └── response/
        └── EtiquetaResponse.java
```

## 🔌 API Endpoints

### Gestión de Etiquetas

#### GET /api/etiquetas
Listar todas las etiquetas.

**Query Params:**
- `tipo` (opcional): INGREDIENTE, EJERCICIO, META, PLAN
- `activo` (opcional): true/false

**Response (200):**
```json
[
  {
    "id": 1,
    "nombre": "Vegano",
    "descripcion": "Productos sin origen animal",
    "tipo": "INGREDIENTE",
    "color": "#4CAF50",
    "activo": true
  },
  {
    "id": 2,
    "nombre": "Cardio",
    "descripcion": "Ejercicios cardiovasculares",
    "tipo": "EJERCICIO",
    "color": "#FF5722",
    "activo": true
  }
]
```

---

#### POST /api/etiquetas
Crear nueva etiqueta (Requiere ADMIN o NUTRITIONIST).

**Request:**
```json
{
  "nombre": "Sin Gluten",
  "descripcion": "Ingredientes libres de gluten",
  "tipo": "INGREDIENTE",
  "color": "#2196F3"
}
```

**Response (201):**
```json
{
  "id": 3,
  "nombre": "Sin Gluten",
  "descripcion": "Ingredientes libres de gluten",
  "tipo": "INGREDIENTE",
  "color": "#2196F3",
  "activo": true,
  "fechaCreacion": "2025-10-31T10:00:00"
}
```

**Validaciones:**
- Nombre único por tipo
- Color en formato hexadecimal
- Tipo válido del enum

---

#### PUT /api/etiquetas/{id}
Actualizar etiqueta existente.

**Request:**
```json
{
  "nombre": "Sin Gluten Certificado",
  "descripcion": "Ingredientes certificados sin gluten",
  "color": "#2196F3",
  "activo": true
}
```

**Response (200):**
```json
{
  "id": 3,
  "nombre": "Sin Gluten Certificado",
  "descripcion": "Ingredientes certificados sin gluten",
  "tipo": "INGREDIENTE",
  "color": "#2196F3",
  "activo": true
}
```

---

#### DELETE /api/etiquetas/{id}
Eliminar etiqueta (soft delete).

**Response (200):**
```json
{
  "message": "Etiqueta eliminada exitosamente"
}
```

**Nota:** Verifica que no esté en uso antes de eliminar.

---

### Asignación de Etiquetas

#### POST /api/ingredientes/{id}/etiquetas
Asignar etiquetas a un ingrediente.

**Request:**
```json
{
  "etiquetaIds": [1, 3, 5]
}
```

**Response (200):**
```json
{
  "ingredienteId": 10,
  "etiquetas": [
    {
      "id": 1,
      "nombre": "Vegano"
    },
    {
      "id": 3,
      "nombre": "Sin Gluten"
    },
    {
      "id": 5,
      "nombre": "Alto en Proteína"
    }
  ]
}
```

---

#### GET /api/ingredientes/{id}/etiquetas
Obtener etiquetas de un ingrediente.

**Response (200):**
```json
[
  {
    "id": 1,
    "nombre": "Vegano",
    "tipo": "INGREDIENTE",
    "color": "#4CAF50"
  }
]
```

---

#### DELETE /api/ingredientes/{ingredienteId}/etiquetas/{etiquetaId}
Remover etiqueta de un ingrediente.

**Response (204):** No Content

---

### Búsqueda por Etiquetas

#### GET /api/ingredientes/buscar-por-etiquetas
Buscar ingredientes por etiquetas.

**Query Params:**
- `etiquetaIds`: 1,3,5
- `operador`: AND/OR (por defecto AND)

**Response (200):**
```json
[
  {
    "id": 10,
    "nombre": "Tofu",
    "etiquetas": ["Vegano", "Alto en Proteína"]
  },
  {
    "id": 15,
    "nombre": "Quinoa",
    "etiquetas": ["Vegano", "Sin Gluten"]
  }
]
```

## 🗄️ Modelo de Datos

### Etiqueta

```java
@Entity
@Table(name = "etiquetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etiqueta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEtiqueta tipo;
    
    @Column(length = 7)
    private String color; // Formato: #RRGGBB
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        ultimaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }
}
```

### Enum TipoEtiqueta

```java
public enum TipoEtiqueta {
    INGREDIENTE("Etiqueta para ingredientes"),
    EJERCICIO("Etiqueta para ejercicios"),
    META("Etiqueta para metas"),
    PLAN("Etiqueta para planes nutricionales");
    
    private final String descripcion;
    
    TipoEtiqueta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

### EtiquetaIngrediente (Tabla de Relación)

```java
@Entity
@Table(name = "etiqueta_ingrediente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetaIngrediente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "etiqueta_id", nullable = false)
    private Etiqueta etiqueta;
    
    @Column(name = "ingrediente_id", nullable = false)
    private Long ingredienteId; // FK a tabla ingredientes
    
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;
    
    @PrePersist
    protected void onCreate() {
        fechaAsignacion = LocalDateTime.now();
    }
}
```

**Nota:** Similar para EtiquetaEjercicio, EtiquetaMeta, y EtiquetaPlan.

## 🔧 Lógica de Negocio

### EtiquetaService

```java
@Service
@Transactional
public class EtiquetaService {
    
    @Autowired
    private EtiquetaRepository etiquetaRepository;
    
    @Autowired
    private EtiquetaIngredienteRepository etiquetaIngredienteRepository;
    
    /**
     * Crear nueva etiqueta con validaciones
     */
    public Etiqueta crear(EtiquetaRequest request) {
        // Validar nombre único por tipo
        if (etiquetaRepository.existsByNombreAndTipo(
                request.getNombre(), request.getTipo())) {
            throw new DuplicateResourceException(
                "Ya existe una etiqueta con ese nombre para ese tipo");
        }
        
        // Validar color
        if (!isValidHexColor(request.getColor())) {
            throw new BusinessRuleException("Color inválido");
        }
        
        Etiqueta etiqueta = Etiqueta.builder()
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .tipo(request.getTipo())
            .color(request.getColor())
            .activo(true)
            .build();
        
        return etiquetaRepository.save(etiqueta);
    }
    
    /**
     * Asignar múltiples etiquetas a un ingrediente
     */
    public void asignarEtiquetasIngrediente(
            Long ingredienteId, 
            List<Long> etiquetaIds) {
        
        // Validar que todas las etiquetas existan y sean del tipo correcto
        List<Etiqueta> etiquetas = etiquetaRepository
            .findAllById(etiquetaIds);
        
        if (etiquetas.size() != etiquetaIds.size()) {
            throw new ResourceNotFoundException(
                "Una o más etiquetas no existen");
        }
        
        // Verificar tipo
        boolean tipoIncorrecto = etiquetas.stream()
            .anyMatch(e -> e.getTipo() != TipoEtiqueta.INGREDIENTE);
        
        if (tipoIncorrecto) {
            throw new BusinessRuleException(
                "Solo se pueden asignar etiquetas de tipo INGREDIENTE");
        }
        
        // Eliminar asignaciones existentes
        etiquetaIngredienteRepository
            .deleteByIngredienteId(ingredienteId);
        
        // Crear nuevas asignaciones
        List<EtiquetaIngrediente> asignaciones = etiquetas.stream()
            .map(etiqueta -> {
                EtiquetaIngrediente ei = new EtiquetaIngrediente();
                ei.setEtiqueta(etiqueta);
                ei.setIngredienteId(ingredienteId);
                return ei;
            })
            .collect(Collectors.toList());
        
        etiquetaIngredienteRepository.saveAll(asignaciones);
    }
    
    /**
     * Buscar ingredientes por etiquetas con operador AND/OR
     */
    public List<Long> buscarIngredientesPorEtiquetas(
            List<Long> etiquetaIds, 
            String operador) {
        
        if ("AND".equalsIgnoreCase(operador)) {
            // Ingredientes que tienen TODAS las etiquetas
            return etiquetaIngredienteRepository
                .findIngredientesConTodasEtiquetas(
                    etiquetaIds, 
                    (long) etiquetaIds.size()
                );
        } else {
            // Ingredientes que tienen AL MENOS UNA etiqueta
            return etiquetaIngredienteRepository
                .findIngredientesPorEtiquetas(etiquetaIds);
        }
    }
    
    /**
     * Validar formato hexadecimal de color
     */
    private boolean isValidHexColor(String color) {
        if (color == null) return false;
        return color.matches("^#([A-Fa-f0-9]{6})$");
    }
    
    /**
     * Obtener estadísticas de uso de etiquetas
     */
    public Map<String, Long> obtenerEstadisticasUso(Long etiquetaId) {
        Map<String, Long> stats = new HashMap<>();
        
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Etiqueta no encontrada"));
        
        switch (etiqueta.getTipo()) {
            case INGREDIENTE:
                stats.put("ingredientes", 
                    etiquetaIngredienteRepository.countByEtiquetaId(etiquetaId));
                break;
            case EJERCICIO:
                stats.put("ejercicios", 
                    etiquetaEjercicioRepository.countByEtiquetaId(etiquetaId));
                break;
            // ... otros tipos
        }
        
        return stats;
    }
}
```

## 📊 Queries Personalizadas

### EtiquetaRepository

```java
@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    
    List<Etiqueta> findByTipo(TipoEtiqueta tipo);
    
    List<Etiqueta> findByActivoTrue();
    
    boolean existsByNombreAndTipo(String nombre, TipoEtiqueta tipo);
    
    @Query("SELECT e FROM Etiqueta e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Etiqueta> buscarPorNombre(@Param("termino") String termino);
    
    @Query("SELECT e FROM Etiqueta e WHERE e.tipo = :tipo AND e.activo = true " +
           "ORDER BY e.nombre ASC")
    List<Etiqueta> findActivasPorTipo(@Param("tipo") TipoEtiqueta tipo);
}
```

### EtiquetaIngredienteRepository

```java
@Repository
public interface EtiquetaIngredienteRepository 
        extends JpaRepository<EtiquetaIngrediente, Long> {
    
    List<EtiquetaIngrediente> findByIngredienteId(Long ingredienteId);
    
    void deleteByIngredienteId(Long ingredienteId);
    
    void deleteByIngredienteIdAndEtiquetaId(
        Long ingredienteId, 
        Long etiquetaId
    );
    
    long countByEtiquetaId(Long etiquetaId);
    
    boolean existsByIngredienteIdAndEtiquetaId(
        Long ingredienteId, 
        Long etiquetaId
    );
    
    /**
     * Buscar ingredientes que tengan AL MENOS UNA de las etiquetas
     */
    @Query("SELECT DISTINCT ei.ingredienteId FROM EtiquetaIngrediente ei " +
           "WHERE ei.etiqueta.id IN :etiquetaIds")
    List<Long> findIngredientesPorEtiquetas(
        @Param("etiquetaIds") List<Long> etiquetaIds
    );
    
    /**
     * Buscar ingredientes que tengan TODAS las etiquetas (AND)
     */
    @Query("SELECT ei.ingredienteId FROM EtiquetaIngrediente ei " +
           "WHERE ei.etiqueta.id IN :etiquetaIds " +
           "GROUP BY ei.ingredienteId " +
           "HAVING COUNT(DISTINCT ei.etiqueta.id) = :cantidad")
    List<Long> findIngredientesConTodasEtiquetas(
        @Param("etiquetaIds") List<Long> etiquetaIds,
        @Param("cantidad") Long cantidad
    );
}
```

## 🧪 Testing

### Test de Creación de Etiqueta

```java
@SpringBootTest
@Transactional
class EtiquetaServiceTest {
    
    @Autowired
    private EtiquetaService etiquetaService;
    
    @Autowired
    private EtiquetaRepository etiquetaRepository;
    
    @Test
    void testCrearEtiqueta_Success() {
        EtiquetaRequest request = EtiquetaRequest.builder()
            .nombre("Vegano")
            .descripcion("Sin productos animales")
            .tipo(TipoEtiqueta.INGREDIENTE)
            .color("#4CAF50")
            .build();
        
        Etiqueta etiqueta = etiquetaService.crear(request);
        
        assertNotNull(etiqueta.getId());
        assertEquals("Vegano", etiqueta.getNombre());
        assertTrue(etiqueta.getActivo());
    }
    
    @Test
    void testCrearEtiqueta_NombreDuplicado() {
        // Crear primera etiqueta
        etiquetaService.crear(EtiquetaRequest.builder()
            .nombre("Vegano")
            .tipo(TipoEtiqueta.INGREDIENTE)
            .color("#4CAF50")
            .build());
        
        // Intentar crear duplicada
        assertThrows(DuplicateResourceException.class, () -> {
            etiquetaService.crear(EtiquetaRequest.builder()
                .nombre("Vegano")
                .tipo(TipoEtiqueta.INGREDIENTE)
                .color("#FF0000")
                .build());
        });
    }
    
    @Test
    void testCrearEtiqueta_ColorInvalido() {
        EtiquetaRequest request = EtiquetaRequest.builder()
            .nombre("Test")
            .tipo(TipoEtiqueta.INGREDIENTE)
            .color("rojo") // Color inválido
            .build();
        
        assertThrows(BusinessRuleException.class, () -> {
            etiquetaService.crear(request);
        });
    }
}
```

### Test de Asignación de Etiquetas

```java
@Test
void testAsignarEtiquetas_Success() {
    // Crear etiquetas de prueba
    Etiqueta etiqueta1 = crearEtiquetaPrueba("Vegano", TipoEtiqueta.INGREDIENTE);
    Etiqueta etiqueta2 = crearEtiquetaPrueba("Sin Gluten", TipoEtiqueta.INGREDIENTE);
    
    Long ingredienteId = 100L;
    List<Long> etiquetaIds = Arrays.asList(etiqueta1.getId(), etiqueta2.getId());
    
    etiquetaService.asignarEtiquetasIngrediente(ingredienteId, etiquetaIds);
    
    List<EtiquetaIngrediente> asignaciones = 
        etiquetaIngredienteRepository.findByIngredienteId(ingredienteId);
    
    assertEquals(2, asignaciones.size());
}

@Test
void testBuscarPorEtiquetas_OperadorAND() {
    // Setup: Ingrediente 1 con etiquetas A y B
    // Ingrediente 2 solo con etiqueta A
    
    List<Long> etiquetaIds = Arrays.asList(etiquetaA.getId(), etiquetaB.getId());
    List<Long> resultados = etiquetaService
        .buscarIngredientesPorEtiquetas(etiquetaIds, "AND");
    
    // Solo ingrediente 1 debe aparecer (tiene ambas)
    assertEquals(1, resultados.size());
    assertTrue(resultados.contains(ingrediente1Id));
}
```

## 🎨 Casos de Uso

### Caso 1: Filtrar Recetas Veganas sin Gluten

```java
// 1. Obtener IDs de etiquetas
Long etiquetaVegano = etiquetaRepository
    .findByNombreAndTipo("Vegano", TipoEtiqueta.INGREDIENTE)
    .getId();

Long etiquetaSinGluten = etiquetaRepository
    .findByNombreAndTipo("Sin Gluten", TipoEtiqueta.INGREDIENTE)
    .getId();

// 2. Buscar ingredientes con ambas etiquetas
List<Long> ingredientesIds = etiquetaService
    .buscarIngredientesPorEtiquetas(
        Arrays.asList(etiquetaVegano, etiquetaSinGluten),
        "AND"
    );

// 3. Buscar recetas que usen esos ingredientes
List<Receta> recetas = recetaService
    .findByIngredientesIn(ingredientesIds);
```

### Caso 2: Sugerir Ejercicios por Categoría

```java
// Buscar etiquetas de tipo ejercicio
List<Etiqueta> categoriasEjercicio = etiquetaRepository
    .findActivasPorTipo(TipoEtiqueta.EJERCICIO);

// Para una categoría específica (ej: Cardio)
Long etiquetaCardio = categoriasEjercicio.stream()
    .filter(e -> e.getNombre().equals("Cardio"))
    .findFirst()
    .map(Etiqueta::getId)
    .orElseThrow();

List<Ejercicio> ejerciciosCardio = ejercicioService
    .findByEtiquetaId(etiquetaCardio);
```

## 📋 Checklist de Implementación

### Fase 1: Estructura Base
- [ ] Crear entidades (Etiqueta, EtiquetaIngrediente, etc.)
- [ ] Crear repositorios
- [ ] Implementar EtiquetaService básico
- [ ] Crear DTOs
- [ ] Implementar EtiquetaController

### Fase 2: Funcionalidades Avanzadas
- [ ] Búsqueda con operadores AND/OR
- [ ] Validación de colores
- [ ] Soft delete
- [ ] Estadísticas de uso

### Fase 3: Integración
- [ ] Integrar con módulo de ingredientes
- [ ] Integrar con módulo de ejercicios
- [ ] Integrar con módulo de metas
- [ ] Integrar con módulo de planes

### Fase 4: Testing y Documentación
- [ ] Tests unitarios (70%+ cobertura)
- [ ] Tests de integración
- [ ] Documentación API (Swagger)
- [ ] Documentación técnica

## 🚀 Mejoras Futuras

- [ ] Sistema de etiquetas jerárquicas (padre-hijo)
- [ ] Sugerencias automáticas de etiquetas con ML
- [ ] Sinónimos de etiquetas
- [ ] Etiquetas privadas por usuario
- [ ] Analytics de popularidad de etiquetas
- [ ] Migración masiva de etiquetas
- [ ] API de autocompletado de etiquetas

## 📚 Referencias

- [Spring Data JPA Many-to-Many](https://www.baeldung.com/jpa-many-to-many)
- [Tag System Design](https://www.scalablepath.com/back-end/tag-system)

---

**Contacto:** [Email del responsable]  
**Issues:** Reportar en GitHub con etiqueta `etiquetas`
