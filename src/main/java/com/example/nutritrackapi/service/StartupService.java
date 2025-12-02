package com.example.nutritrackapi.service;

import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartupService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CuentaAuthRepository cuentaAuthRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final UsuarioPerfilSaludRepository usuarioPerfilSaludRepository;
    private final UsuarioHistorialMedidasRepository usuarioHistorialMedidasRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final PasswordEncoder passwordEncoder;

    // Repos de catálogo
    private final EtiquetaRepository etiquetaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final EjercicioRepository ejercicioRepository;
    private final ComidaRepository comidaRepository;
    private final ComidaIngredienteRepository comidaIngredienteRepository;
    private final PlanRepository planRepository;
    private final PlanObjetivoRepository planObjetivoRepository;
    private final PlanDiaRepository planDiaRepository;
    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Iniciando NutriTrack API...");

        initializeRoles();
        initializeTiposComida();

        initializeEtiquetas();
        initializeIngredientes();
        initializeEjercicios();
        initializeComidas();
        initializePlanes();
        initializeRutinas();

        initializeAdminUser();
        initializeDemoUser();
        initializeDemoData();

        log.info("✅ Aplicación lista!");
    }

    // ============================================================
    // ROLES
    // ============================================================

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            log.info("📝 Creando roles por defecto...");

            Role userRole = Role.builder()
                    .tipoRol(Role.TipoRol.ROLE_USER)
                    .build();

            Role adminRole = Role.builder()
                    .tipoRol(Role.TipoRol.ROLE_ADMIN)
                    .build();

            roleRepository.save(userRole);
            roleRepository.save(adminRole);

            log.info("✅ Roles creados: ROLE_USER, ROLE_ADMIN");
        } else {
            log.info("ℹ️ Roles ya existen en la base de datos");
        }
    }

    // ============================================================
    // TIPOS DE COMIDA
    // ============================================================

    private void initializeTiposComida() {
        if (tipoComidaRepository.count() == 0) {
            log.info("🍽️ Creando tipos de comida por defecto...");

            String[][] tiposComida = {
                    {"DESAYUNO", "Primera comida del día", "1"},
                    {"ALMUERZO", "Comida del mediodía", "2"},
                    {"CENA", "Última comida principal del día", "3"},
                    {"SNACK", "Merienda o bocadillo", "4"},
                    {"MERIENDA", "Comida ligera entre comidas principales", "5"},
                    {"PRE_ENTRENAMIENTO", "Comida antes del ejercicio", "6"},
                    {"POST_ENTRENAMIENTO", "Comida después del ejercicio", "7"},
                    {"COLACION", "Refrigerio ligero", "8"}
            };

            for (String[] tipo : tiposComida) {
                TipoComidaEntity tipoComida = TipoComidaEntity.builder()
                        .nombre(tipo[0])
                        .descripcion(tipo[1])
                        .ordenVisualizacion(Integer.parseInt(tipo[2]))
                        .activo(true)
                        .build();
                tipoComidaRepository.save(tipoComida);
            }

            log.info("✅ Tipos de comida creados");
        } else {
            log.info("ℹ️ Tipos de comida ya existen en la base de datos");
        }
    }

    // ============================================================
    // ETIQUETAS
    // ============================================================

    private void initializeEtiquetas() {
        if (etiquetaRepository.count() > 0) {
            log.info("ℹ️ Etiquetas ya existen, no se regeneran");
            return;
        }

        log.info("🏷️ Creando etiquetas iniciales...");

        // Alergias
        etiquetaRepository.save(crearEtiqueta("Sin gluten", Etiqueta.TipoEtiqueta.ALERGIA,
                "Apto para personas con intolerancia al gluten"));
        etiquetaRepository.save(crearEtiqueta("Sin lactosa", Etiqueta.TipoEtiqueta.ALERGIA,
                "Apto para personas con intolerancia a la lactosa"));
        etiquetaRepository.save(crearEtiqueta("Sin frutos secos", Etiqueta.TipoEtiqueta.ALERGIA,
                "Apto para personas con alergia a frutos secos"));

        // Condiciones médicas
        etiquetaRepository.save(crearEtiqueta("Apto para diabéticos", Etiqueta.TipoEtiqueta.CONDICION_MEDICA,
                "Adecuado para control de glucosa"));
        etiquetaRepository.save(crearEtiqueta("Bajo en sodio", Etiqueta.TipoEtiqueta.CONDICION_MEDICA,
                "Recomendado para hipertensión"));

        // Objetivos
        etiquetaRepository.save(crearEtiqueta("Pérdida de peso", Etiqueta.TipoEtiqueta.OBJETIVO,
                "Orientado a déficit calórico moderado"));
        etiquetaRepository.save(crearEtiqueta("Ganancia muscular", Etiqueta.TipoEtiqueta.OBJETIVO,
                "Alto en proteínas y ligero superávit calórico"));
        etiquetaRepository.save(crearEtiqueta("Mantenimiento", Etiqueta.TipoEtiqueta.OBJETIVO,
                "Mantener peso corporal actual"));

        // Dietas
        etiquetaRepository.save(crearEtiqueta("Alta en proteína", Etiqueta.TipoEtiqueta.DIETA,
                "Prioriza alimentos ricos en proteína"));
        etiquetaRepository.save(crearEtiqueta("Vegetariano", Etiqueta.TipoEtiqueta.DIETA,
                "Sin carnes, puede incluir lácteos y huevos"));
        etiquetaRepository.save(crearEtiqueta("Vegano", Etiqueta.TipoEtiqueta.DIETA,
                "Sin productos de origen animal"));
        etiquetaRepository.save(crearEtiqueta("Mediterráneo", Etiqueta.TipoEtiqueta.DIETA,
                "Basado en frutas, verduras, cereales integrales y aceite de oliva"));

        // Dificultad
        etiquetaRepository.save(crearEtiqueta("Principiante", Etiqueta.TipoEtiqueta.DIFICULTAD,
                "Baja intensidad, ideal para iniciar"));
        etiquetaRepository.save(crearEtiqueta("Intermedio", Etiqueta.TipoEtiqueta.DIFICULTAD,
                "Intensidad moderada"));
        etiquetaRepository.save(crearEtiqueta("Avanzado", Etiqueta.TipoEtiqueta.DIFICULTAD,
                "Mayor exigencia física"));

        // Tipo ejercicio
        etiquetaRepository.save(crearEtiqueta("Cardio", Etiqueta.TipoEtiqueta.TIPO_EJERCICIO,
                "Ejercicio aeróbico"));
        etiquetaRepository.save(crearEtiqueta("Fuerza", Etiqueta.TipoEtiqueta.TIPO_EJERCICIO,
                "Entrenamiento de fuerza"));
        etiquetaRepository.save(crearEtiqueta("HIIT", Etiqueta.TipoEtiqueta.TIPO_EJERCICIO,
                "Intervalos de alta intensidad"));
        etiquetaRepository.save(crearEtiqueta("Yoga", Etiqueta.TipoEtiqueta.TIPO_EJERCICIO,
                "Trabajo de flexibilidad, respiración y control"));

        // Grupo muscular
        etiquetaRepository.save(crearEtiqueta("Piernas", Etiqueta.TipoEtiqueta.GRUPO_MUSCULAR,
                "Trabajo de tren inferior"));
        etiquetaRepository.save(crearEtiqueta("Espalda", Etiqueta.TipoEtiqueta.GRUPO_MUSCULAR,
                "Trabajo de espalda"));
        etiquetaRepository.save(crearEtiqueta("Pecho", Etiqueta.TipoEtiqueta.GRUPO_MUSCULAR,
                "Trabajo de pectorales"));
        etiquetaRepository.save(crearEtiqueta("Core", Etiqueta.TipoEtiqueta.GRUPO_MUSCULAR,
                "Trabajo de abdominales y zona media"));

        log.info("✅ Etiquetas iniciales creadas");
    }

    private Etiqueta crearEtiqueta(String nombre, Etiqueta.TipoEtiqueta tipo, String descripcion) {
        return Etiqueta.builder()
                .nombre(nombre)
                .tipoEtiqueta(tipo)
                .descripcion(descripcion)
                .build(); // createdAt/updatedAt los rellena el auditing
    }

    // ============================================================
    // INGREDIENTES
    // ============================================================

    private void initializeIngredientes() {
        if (ingredienteRepository.count() > 0) {
            log.info("ℹ️ Ingredientes ya existen, no se regeneran");
            return;
        }

        log.info("🥑 Creando ingredientes iniciales...");

        ingredienteRepository.save(crearIngrediente(
                "Pechuga de pollo",
                31, 0, 3.6, 165,
                0.0,
                Ingrediente.CategoriaAlimento.PROTEINAS,
                "Pechuga de pollo sin piel, a la plancha"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Arroz blanco cocido",
                2.7, 28, 0.3, 130,
                0.4,
                Ingrediente.CategoriaAlimento.CEREALES,
                "Arroz blanco cocido, porción estándar"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Avena integral",
                13, 68, 7, 389,
                10.0,
                Ingrediente.CategoriaAlimento.CEREALES,
                "Avena integral en hojuelas"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Leche descremada",
                3.4, 5, 0.2, 42,
                0.0,
                Ingrediente.CategoriaAlimento.LACTEOS,
                "Leche de vaca descremada"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Plátano",
                1.3, 23, 0.3, 96,
                2.6,
                Ingrediente.CategoriaAlimento.FRUTAS,
                "Plátano maduro"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Manzana",
                0.3, 14, 0.2, 52,
                2.4,
                Ingrediente.CategoriaAlimento.FRUTAS,
                "Manzana roja con cáscara"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Brócoli",
                2.8, 7, 0.4, 34,
                2.6,
                Ingrediente.CategoriaAlimento.VERDURAS,
                "Brócoli cocido al vapor"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Zanahoria",
                0.9, 10, 0.2, 41,
                2.8,
                Ingrediente.CategoriaAlimento.VERDURAS,
                "Zanahoria cruda"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Aceite de oliva",
                0, 0, 100, 884,
                0.0,
                Ingrediente.CategoriaAlimento.GRASAS_SALUDABLES,
                "Aceite de oliva extra virgen"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Huevo",
                13, 1.1, 11, 155,
                0.0,
                Ingrediente.CategoriaAlimento.PROTEINAS,
                "Huevo de gallina entero"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Yogur griego light",
                10, 4, 0.4, 59,
                0.0,
                Ingrediente.CategoriaAlimento.LACTEOS,
                "Yogur griego bajo en grasa"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Pan integral",
                9, 49, 4.2, 265,
                7.0,
                Ingrediente.CategoriaAlimento.CEREALES,
                "Pan integral de trigo"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Queso fresco bajo en grasa",
                18, 3, 5, 145,
                0.0,
                Ingrediente.CategoriaAlimento.LACTEOS,
                "Queso fresco descremado"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Atún en agua",
                23, 0, 1, 109,
                0.0,
                Ingrediente.CategoriaAlimento.PROTEINAS,
                "Atún enlatado en agua"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Lentejas cocidas",
                9, 20, 0.4, 116,
                8.0,
                Ingrediente.CategoriaAlimento.LEGUMBRES,
                "Lentejas cocidas en agua"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Garbanzos cocidos",
                8.9, 27, 2.6, 164,
                7.6,
                Ingrediente.CategoriaAlimento.LEGUMBRES,
                "Garbanzos cocidos"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Almendras",
                21, 22, 50, 579,
                12.5,
                Ingrediente.CategoriaAlimento.FRUTOS_SECOS,
                "Almendras naturales"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Mantequilla de maní",
                25, 20, 50, 588,
                6.0,
                Ingrediente.CategoriaAlimento.GRASAS_SALUDABLES,
                "Crema de maní sin azúcar añadida"
        ));

        ingredienteRepository.save(crearIngrediente(
                "Espinaca",
                2.9, 3.6, 0.4, 23,
                2.2,
                Ingrediente.CategoriaAlimento.VERDURAS,
                "Espinaca cruda"
        ));

        log.info("✅ Ingredientes iniciales creados ({} registros)", ingredienteRepository.count());
    }

    private Ingrediente crearIngrediente(String nombre,
                                         double proteinas,
                                         double carbohidratos,
                                         double grasas,
                                         double energia,
                                         Double fibra,
                                         Ingrediente.CategoriaAlimento categoria,
                                         String descripcion) {
        return Ingrediente.builder()
                .nombre(nombre)
                .proteinas(BigDecimal.valueOf(proteinas))
                .carbohidratos(BigDecimal.valueOf(carbohidratos))
                .grasas(BigDecimal.valueOf(grasas))
                .energia(BigDecimal.valueOf(energia))
                .fibra(fibra != null ? BigDecimal.valueOf(fibra) : null)
                .categoriaAlimento(categoria)
                .descripcion(descripcion)
                .build();
    }

    // ============================================================
    // EJERCICIOS
    // ============================================================

    private void initializeEjercicios() {
        if (ejercicioRepository.count() > 0) {
            log.info("ℹ️ Ejercicios ya existen, no se regeneran");
            return;
        }

        log.info("🏋️ Creando ejercicios iniciales...");

        ejercicioRepository.save(crearEjercicio(
                "Correr en cinta",
                "Carrera continua a ritmo moderado en cinta",
                Ejercicio.TipoEjercicio.CARDIO,
                Ejercicio.GrupoMuscular.CARDIO,
                Ejercicio.NivelDificultad.INTERMEDIO,
                10.0,
                30,
                "Cinta de correr"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Bicicleta estática",
                "Pedaleo continuo en bicicleta estática",
                Ejercicio.TipoEjercicio.CARDIO,
                Ejercicio.GrupoMuscular.CARDIO,
                Ejercicio.NivelDificultad.PRINCIPIANTE,
                8.0,
                30,
                "Bicicleta estática"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Caminata rápida",
                "Caminar a paso rápido",
                Ejercicio.TipoEjercicio.CARDIO,
                Ejercicio.GrupoMuscular.CARDIO,
                Ejercicio.NivelDificultad.PRINCIPIANTE,
                5.0,
                30,
                "Ninguno"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Sentadilla con barra",
                "Sentadilla profunda con barra en espalda",
                Ejercicio.TipoEjercicio.FUERZA,
                Ejercicio.GrupoMuscular.PIERNAS,
                Ejercicio.NivelDificultad.INTERMEDIO,
                7.0,
                20,
                "Barra olímpica, discos, rack"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Peso muerto convencional",
                "Levantamiento desde el suelo con barra",
                Ejercicio.TipoEjercicio.FUERZA,
                Ejercicio.GrupoMuscular.ESPALDA,
                Ejercicio.NivelDificultad.AVANZADO,
                8.0,
                20,
                "Barra olímpica y discos"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Press de banca",
                "Press de pecho acostado en banco",
                Ejercicio.TipoEjercicio.FUERZA,
                Ejercicio.GrupoMuscular.PECHO,
                Ejercicio.NivelDificultad.INTERMEDIO,
                6.0,
                20,
                "Banco plano y barra"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Press militar de pie",
                "Press por encima de la cabeza con barra",
                Ejercicio.TipoEjercicio.FUERZA,
                Ejercicio.GrupoMuscular.HOMBROS,
                Ejercicio.NivelDificultad.INTERMEDIO,
                6.5,
                20,
                "Barra o mancuernas"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Remo con barra",
                "Remo inclinado con barra para espalda",
                Ejercicio.TipoEjercicio.FUERZA,
                Ejercicio.GrupoMuscular.ESPALDA,
                Ejercicio.NivelDificultad.INTERMEDIO,
                6.5,
                20,
                "Barra y discos"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Plancha",
                "Plancha isométrica apoyando antebrazos",
                Ejercicio.TipoEjercicio.FUNCIONAL,
                Ejercicio.GrupoMuscular.CORE,
                Ejercicio.NivelDificultad.PRINCIPIANTE,
                4.0,
                10,
                "Colchoneta"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Abdominales crunch",
                "Crunch abdominal clásico",
                Ejercicio.TipoEjercicio.FUNCIONAL,
                Ejercicio.GrupoMuscular.ABDOMINALES,
                Ejercicio.NivelDificultad.PRINCIPIANTE,
                4.0,
                10,
                "Colchoneta"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Yoga vinyasa",
                "Secuencia fluida de posturas de yoga",
                Ejercicio.TipoEjercicio.YOGA,
                Ejercicio.GrupoMuscular.CUERPO_COMPLETO,
                Ejercicio.NivelDificultad.INTERMEDIO,
                5.0,
                30,
                "Colchoneta"
        ));

        ejercicioRepository.save(crearEjercicio(
                "Burpees",
                "Burpee completo con salto y flexión",
                Ejercicio.TipoEjercicio.HIIT,
                Ejercicio.GrupoMuscular.CUERPO_COMPLETO,
                Ejercicio.NivelDificultad.AVANZADO,
                12.0,
                10,
                "Ninguno"
        ));

        log.info("✅ Ejercicios iniciales creados ({} registros)", ejercicioRepository.count());
    }

    private Ejercicio crearEjercicio(String nombre,
                                     String descripcion,
                                     Ejercicio.TipoEjercicio tipo,
                                     Ejercicio.GrupoMuscular grupo,
                                     Ejercicio.NivelDificultad nivel,
                                     double caloriasMin,
                                     Integer duracionEstimada,
                                     String equipo) {
        return Ejercicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .tipoEjercicio(tipo)
                .grupoMuscular(grupo)
                .nivelDificultad(nivel)
                .caloriasQuemadasPorMinuto(BigDecimal.valueOf(caloriasMin))
                .duracionEstimadaMinutos(duracionEstimada)
                .equipoNecesario(equipo)
                .build();
    }

    // ============================================================
    // COMIDAS + RECETAS
    // ============================================================

    private void initializeComidas() {
        if (comidaRepository.count() > 0) {
            log.info("ℹ️ Comidas ya existen, no se regeneran");
            return;
        }

        log.info("🍲 Creando comidas y recetas iniciales...");

        // Mapear ingredientes y tipos de comida
        Map<String, Ingrediente> ingredientes = new HashMap<>();
        ingredienteRepository.findAll().forEach(ing -> ingredientes.put(ing.getNombre(), ing));

        Map<String, TipoComidaEntity> tiposComida = new HashMap<>();
        tipoComidaRepository.findAll().forEach(t -> tiposComida.put(t.getNombre(), t));

        // Crear comidas base
        Comida avenaBanano = comidaRepository.save(crearComida(
                "Avena con plátano y mantequilla de maní",
                tiposComida.get("DESAYUNO"),
                "Desayuno alto en energía y proteína.",
                10,
                1,
                "Cocinar la avena con leche, servir y agregar plátano en rodajas y mantequilla de maní por encima."
        ));

        Comida polloArrozBrocoli = comidaRepository.save(crearComida(
                "Pollo a la plancha con arroz y brócoli",
                tiposComida.get("ALMUERZO"),
                "Plato balanceado rico en proteína y carbohidratos complejos.",
                25,
                1,
                "Cocinar el arroz, saltear el brócoli al vapor y dorar el pollo a la plancha con poco aceite."
        ));

        Comida ensaladaAtun = comidaRepository.save(crearComida(
                "Ensalada de atún con garbanzos",
                tiposComida.get("CENA"),
                "Ensalada fresca rica en proteína y fibra.",
                15,
                1,
                "Mezclar atún en agua, garbanzos cocidos, zanahoria rallada y espinaca. Aliñar con aceite de oliva."
        ));

        Comida yogurSnack = comidaRepository.save(crearComida(
                "Yogur griego con almendras y manzana",
                tiposComida.get("SNACK"),
                "Snack alto en proteína y grasas saludables.",
                5,
                1,
                "Servir el yogur en un bol, añadir manzana en cubos y almendras picadas."
        ));

        Comida omeletteEspinaca = comidaRepository.save(crearComida(
                "Omelette de claras con espinaca y pan integral",
                tiposComida.get("DESAYUNO"),
                "Desayuno alto en proteína y bajo en grasas.",
                15,
                1,
                "Batir claras de huevo, agregar espinaca picada y cocinar en sartén antiadherente. Acompañar con pan integral tostado."
        ));

        Comida lentejasArroz = comidaRepository.save(crearComida(
                "Lentejas guisadas con arroz",
                tiposComida.get("ALMUERZO"),
                "Plato vegetariano rico en proteína vegetal y carbohidratos complejos.",
                35,
                1,
                "Cocinar las lentejas con condimentos al gusto y acompañar con arroz blanco cocido."
        ));

        // Recetas (relación ComidaIngrediente)

        // Avena con plátano
        addIngredienteToComida(avenaBanano, "Avena integral", 50, "Avena en hojuelas", ingredientes);
        addIngredienteToComida(avenaBanano, "Leche descremada", 200, "Puede reemplazarse por bebida vegetal", ingredientes);
        addIngredienteToComida(avenaBanano, "Plátano", 80, "Plátano pequeño", ingredientes);
        addIngredienteToComida(avenaBanano, "Mantequilla de maní", 20, "Sin azúcar añadida", ingredientes);

        // Pollo con arroz y brócoli
        addIngredienteToComida(polloArrozBrocoli, "Pechuga de pollo", 150, "Sin piel", ingredientes);
        addIngredienteToComida(polloArrozBrocoli, "Arroz blanco cocido", 150, "Porción estándar", ingredientes);
        addIngredienteToComida(polloArrozBrocoli, "Brócoli", 80, "Cocido al vapor", ingredientes);
        addIngredienteToComida(polloArrozBrocoli, "Aceite de oliva", 10, "Para cocinar el pollo", ingredientes);

        // Ensalada de atún
        addIngredienteToComida(ensaladaAtun, "Atún en agua", 100, "Escurrir bien", ingredientes);
        addIngredienteToComida(ensaladaAtun, "Garbanzos cocidos", 80, "Enjuagados", ingredientes);
        addIngredienteToComida(ensaladaAtun, "Zanahoria", 40, "Rallada", ingredientes);
        addIngredienteToComida(ensaladaAtun, "Espinaca", 40, "Fresca", ingredientes);
        addIngredienteToComida(ensaladaAtun, "Aceite de oliva", 10, "Aliño", ingredientes);

        // Snack yogur
        addIngredienteToComida(yogurSnack, "Yogur griego light", 170, "Envase individual", ingredientes);
        addIngredienteToComida(yogurSnack, "Manzana", 80, "Media manzana", ingredientes);
        addIngredienteToComida(yogurSnack, "Almendras", 15, "Picadas", ingredientes);

        // Omelette
        addIngredienteToComida(omeletteEspinaca, "Huevo", 120, "Aprox 4 claras + 1 yema", ingredientes);
        addIngredienteToComida(omeletteEspinaca, "Espinaca", 30, "Picada", ingredientes);
        addIngredienteToComida(omeletteEspinaca, "Pan integral", 40, "1 tajada", ingredientes);

        // Lentejas con arroz
        addIngredienteToComida(lentejasArroz, "Lentejas cocidas", 150, "Guisadas", ingredientes);
        addIngredienteToComida(lentejasArroz, "Arroz blanco cocido", 120, "Guarnición", ingredientes);
        addIngredienteToComida(lentejasArroz, "Aceite de oliva", 10, "Para el sofrito", ingredientes);

        log.info("✅ Comidas y recetas iniciales creadas ({} comidas)", comidaRepository.count());
    }

    private Comida crearComida(String nombre,
                               TipoComidaEntity tipoComida,
                               String descripcion,
                               int tiempoPrepMin,
                               int porciones,
                               String instrucciones) {
        return Comida.builder()
                .nombre(nombre)
                .tipoComida(tipoComida)
                .descripcion(descripcion)
                .tiempoPreparacionMinutos(tiempoPrepMin)
                .porciones(porciones)
                .instrucciones(instrucciones)
                .comidaIngredientes(new HashSet<>())
                .etiquetas(new HashSet<>())
                .build();
    }

    private void addIngredienteToComida(Comida comida,
                                        String nombreIngrediente,
                                        double gramos,
                                        String notas,
                                        Map<String, Ingrediente> ingredientes) {
        Ingrediente ing = ingredientes.get(nombreIngrediente);
        if (ing == null) {
            log.warn("⚠️ Ingrediente '{}' no encontrado, se omite en comida '{}'",
                    nombreIngrediente, comida.getNombre());
            return;
        }

        ComidaIngrediente.ComidaIngredienteId id =
                new ComidaIngrediente.ComidaIngredienteId(comida.getId(), ing.getId());

        ComidaIngrediente ci = ComidaIngrediente.builder()
                .id(id)
                .comida(comida)
                .ingrediente(ing)
                .cantidadGramos(BigDecimal.valueOf(gramos))
                .notas(notas)
                .build();

        comidaIngredienteRepository.save(ci);
    }

    // ============================================================
    // PLAN NUTRICIONAL
    // ============================================================

    private void initializePlanes() {
        if (planRepository.count() > 0) {
            log.info("ℹ️ Planes ya existen, no se regeneran");
            return;
        }

        log.info("📅 Creando plan nutricional de ejemplo...");

        Map<String, Etiqueta> etiquetas = new HashMap<>();
        etiquetaRepository.findAll().forEach(e -> etiquetas.put(e.getNombre(), e));

        Map<String, Comida> comidas = new HashMap<>();
        comidaRepository.findAll().forEach(c -> comidas.put(c.getNombre(), c));

        Map<String, TipoComidaEntity> tiposComida = new HashMap<>();
        tipoComidaRepository.findAll().forEach(t -> tiposComida.put(t.getNombre(), t));

        Plan planDeficit7Dias = Plan.builder()
                .nombre("Plan déficit calórico 7 días")
                .descripcion("Plan nutricional de 7 días orientado a pérdida de peso moderada, alto en proteína.")
                .duracionDias(7)
                .activo(true)
                .dias(new HashSet<>())
                .etiquetas(new HashSet<>())
                .build();

        if (etiquetas.containsKey("Pérdida de peso")) {
            planDeficit7Dias.getEtiquetas().add(etiquetas.get("Pérdida de peso"));
        }
        if (etiquetas.containsKey("Alta en proteína")) {
            planDeficit7Dias.getEtiquetas().add(etiquetas.get("Alta en proteína"));
        }
        if (etiquetas.containsKey("Mediterráneo")) {
            planDeficit7Dias.getEtiquetas().add(etiquetas.get("Mediterráneo"));
        }

        planDeficit7Dias = planRepository.save(planDeficit7Dias);

        PlanObjetivo objetivo = PlanObjetivo.builder()
                .plan(planDeficit7Dias)
                .caloriasObjetivo(BigDecimal.valueOf(1900))
                .proteinasObjetivo(BigDecimal.valueOf(130))
                .carbohidratosObjetivo(BigDecimal.valueOf(190))
                .grasasObjetivo(BigDecimal.valueOf(60))
                .descripcion("Objetivo diario aproximado para déficit calórico moderado.")
                .build();

        planObjetivoRepository.save(objetivo);

        for (int dia = 1; dia <= 7; dia++) {
            // Desayuno
            if (dia % 2 == 1) {
                crearPlanDia(planDeficit7Dias, dia, "DESAYUNO",
                        "Avena con plátano y mantequilla de maní",
                        "Desayuno energético.",
                        comidas, tiposComida);
            } else {
                crearPlanDia(planDeficit7Dias, dia, "DESAYUNO",
                        "Omelette de claras con espinaca y pan integral",
                        "Desayuno alto en proteína.",
                        comidas, tiposComida);
            }

            // Almuerzo
            if (dia <= 4) {
                crearPlanDia(planDeficit7Dias, dia, "ALMUERZO",
                        "Pollo a la plancha con arroz y brócoli",
                        "Plato principal rico en proteína.",
                        comidas, tiposComida);
            } else {
                crearPlanDia(planDeficit7Dias, dia, "ALMUERZO",
                        "Lentejas guisadas con arroz",
                        "Alternativa vegetariana rica en fibra.",
                        comidas, tiposComida);
            }

            // Cena
            crearPlanDia(planDeficit7Dias, dia, "CENA",
                    "Ensalada de atún con garbanzos",
                    "Cena ligera y saciante.",
                    comidas, tiposComida);

            // Snack
            crearPlanDia(planDeficit7Dias, dia, "SNACK",
                    "Yogur griego con almendras y manzana",
                    "Snack entre comidas.",
                    comidas, tiposComida);
        }

        log.info("✅ Plan nutricional creado: {}", planDeficit7Dias.getNombre());
    }

    private void crearPlanDia(Plan plan,
                              Integer numeroDia,
                              String tipoComidaNombre,
                              String comidaNombre,
                              String notas,
                              Map<String, Comida> comidas,
                              Map<String, TipoComidaEntity> tiposComida) {

        Comida comida = comidas.get(comidaNombre);
        TipoComidaEntity tipo = tiposComida.get(tipoComidaNombre);

        if (comida == null || tipo == null) {
            log.warn("⚠️ No se pudo crear PlanDia: comida='{}', tipo='{}'", comidaNombre, tipoComidaNombre);
            return;
        }

        PlanDia dia = PlanDia.builder()
                .numeroDia(numeroDia)
                .tipoComida(tipo)
                .notas(notas)
                .comida(comida)
                .plan(plan)
                .build();

        planDiaRepository.save(dia);
        plan.getDias().add(dia);
    }

    // ============================================================
    // RUTINA DE ENTRENAMIENTO
    // ============================================================

    private void initializeRutinas() {
        if (rutinaRepository.count() > 0) {
            log.info("ℹ️ Rutinas ya existen, no se regeneran");
            return;
        }

        log.info("💪 Creando rutina de entrenamiento de ejemplo...");

        Map<String, Ejercicio> ejercicios = new HashMap<>();
        ejercicioRepository.findAll().forEach(e -> ejercicios.put(e.getNombre(), e));

        Map<String, Etiqueta> etiquetas = new HashMap<>();
        etiquetaRepository.findAll().forEach(e -> etiquetas.put(e.getNombre(), e));

        Rutina rutina = Rutina.builder()
                .nombre("Rutina full body 3x semana - 8 semanas")
                .descripcion("Rutina de cuerpo completo para 3 días a la semana, orientada a recomposición corporal.")
                .duracionSemanas(8)
                .patronSemanas(2)
                .nivelDificultad(Ejercicio.NivelDificultad.INTERMEDIO)
                .activo(true)
                .ejercicios(new HashSet<>())
                .etiquetas(new HashSet<>())
                .build();

        if (etiquetas.containsKey("Ganancia muscular")) {
            rutina.getEtiquetas().add(etiquetas.get("Ganancia muscular"));
        }
        if (etiquetas.containsKey("Intermedio")) {
            rutina.getEtiquetas().add(etiquetas.get("Intermedio"));
        }

        rutina = rutinaRepository.save(rutina);

        // Semana base 1 - Día 1 (Lunes)
        crearRutinaEjercicio(rutina, ejercicios, 1, 1, 1,
                "Press de banca", 4, 8, 40, 90, 40.0,
                "Mantener técnica estricta.");
        crearRutinaEjercicio(rutina, ejercicios, 1, 1, 2,
                "Press militar de pie", 3, 10, 30, 90, 25.0,
                "No arquear demasiado la espalda.");
        crearRutinaEjercicio(rutina, ejercicios, 1, 1, 3,
                "Abdominales crunch", 3, 15, 15, 60, null,
                "Controlar la respiración.");

        // Semana base 1 - Día 3 (Miércoles)
        crearRutinaEjercicio(rutina, ejercicios, 1, 3, 1,
                "Peso muerto convencional", 4, 6, 35, 120, 60.0,
                "Priorizar técnica sobre peso.");
        crearRutinaEjercicio(rutina, ejercicios, 1, 3, 2,
                "Remo con barra", 3, 10, 25, 90, 30.0,
                "Mantener espalda recta.");
        crearRutinaEjercicio(rutina, ejercicios, 1, 3, 3,
                "Plancha", 3, 30, 10, 60, null,
                "Mantener abdomen contraído.");

        // Semana base 1 - Día 5 (Viernes)
        crearRutinaEjercicio(rutina, ejercicios, 1, 5, 1,
                "Sentadilla con barra", 4, 8, 35, 120, 50.0,
                "Bajar hasta 90 grados o más si es posible.");
        crearRutinaEjercicio(rutina, ejercicios, 1, 5, 2,
                "Caminata rápida", 1, 1, 20, 0, null,
                "Caminar a ritmo moderado para enfriar.");

        // Semana base 2 - variante
        crearRutinaEjercicio(rutina, ejercicios, 2, 1, 1,
                "Press de banca", 3, 10, 30, 90, 35.0,
                "Semana ligera, menos peso.");
        crearRutinaEjercicio(rutina, ejercicios, 2, 3, 1,
                "Bicicleta estática", 1, 1, 25, 0, null,
                "Cardio moderado.");
        crearRutinaEjercicio(rutina, ejercicios, 2, 5, 1,
                "Sentadilla con barra", 3, 10, 30, 120, 45.0,
                "Enfoque en volumen.");

        log.info("✅ Rutina de entrenamiento creada: {}", rutina.getNombre());
    }

    private void crearRutinaEjercicio(Rutina rutina,
                                      Map<String, Ejercicio> ejercicios,
                                      int semanaBase,
                                      int diaSemana,
                                      int orden,
                                      String nombreEjercicio,
                                      int series,
                                      int repeticiones,
                                      Integer duracionMin,
                                      Integer descansoSeg,
                                      Double peso,
                                      String notas) {

        Ejercicio ej = ejercicios.get(nombreEjercicio);
        if (ej == null) {
            log.warn("⚠️ Ejercicio '{}' no encontrado, se omite en rutina '{}'",
                    nombreEjercicio, rutina.getNombre());
            return;
        }

        RutinaEjercicio re = RutinaEjercicio.builder()
                .semanaBase(semanaBase)
                .diaSemana(diaSemana)
                .orden(orden)
                .series(series)
                .repeticiones(repeticiones)
                .duracionMinutos(duracionMin)
                .descansoSegundos(descansoSeg)
                .peso(peso != null ? BigDecimal.valueOf(peso) : null)
                .notas(notas)
                .ejercicio(ej)
                .rutina(rutina)
                .build();

        rutinaEjercicioRepository.save(re);
        rutina.getEjercicios().add(re);
    }

    // ============================================================
    // USUARIO ADMIN
    // ============================================================

    private void initializeAdminUser() {
        String adminEmail = "admin@nutritrack.com";

        if (cuentaAuthRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("👤 Creando usuario administrador inicial...");

            Role adminRole = roleRepository.findByTipoRol(Role.TipoRol.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));

            CuentaAuth cuentaAuth = CuentaAuth.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin123!"))
                    .active(true)
                    .createdAt(LocalDate.now())
                    .role(adminRole)
                    .build();

            cuentaAuth = cuentaAuthRepository.save(cuentaAuth);

            PerfilUsuario perfil = PerfilUsuario.builder()
                    .nombre("Administrador")
                    .apellido("Sistema")
                    .cuenta(cuentaAuth)
                    .unidadesMedida(PerfilUsuario.UnidadesMedida.KG)
                    .fechaInicioApp(LocalDate.now())
                    .build();

            perfilUsuarioRepository.save(perfil);

            log.info("✅ Usuario administrador creado:");
            log.info("   📧 Email: {}", adminEmail);
            log.info("   🔑 Password: Admin123!");
            log.info("   ⚠️  IMPORTANTE: Cambia esta contraseña en producción");
        } else {
            log.info("ℹ️ Usuario administrador ya existe");
        }
    }

    // ============================================================
    // USUARIO DEMO
    // ============================================================

    private void initializeDemoUser() {
        String demoEmail = "demo@nutritrack.com";

        if (cuentaAuthRepository.findByEmail(demoEmail).isEmpty()) {
            log.info("👤 Creando usuario demo para pruebas...");

            Role userRole = roleRepository.findByTipoRol(Role.TipoRol.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));

            CuentaAuth cuentaAuth = CuentaAuth.builder()
                    .email(demoEmail)
                    .password(passwordEncoder.encode("Demo123!"))
                    .active(true)
                    .createdAt(LocalDate.now())
                    .role(userRole)
                    .build();

            cuentaAuth = cuentaAuthRepository.save(cuentaAuth);

            PerfilUsuario perfil = PerfilUsuario.builder()
                    .nombre("Usuario")
                    .apellido("Demo")
                    .cuenta(cuentaAuth)
                    .unidadesMedida(PerfilUsuario.UnidadesMedida.KG)
                    .fechaInicioApp(LocalDate.now())
                    .build();

            perfilUsuarioRepository.save(perfil);

            log.info("✅ Usuario demo creado:");
            log.info("   📧 Email: {}", demoEmail);
            log.info("   🔑 Password: Demo123!");
        } else {
            log.info("ℹ️ Usuario demo ya existe");
        }
    }

    // ============================================================
    // DATOS DEMO: PERFIL SALUD + MEDICIONES
    // ============================================================

    private void initializeDemoData() {
        if (usuarioPerfilSaludRepository.count() > 0) {
            log.info("ℹ️ Datos de demostración ya existen");
            return;
        }

        log.info("📊 Cargando datos de demostración...");

        CuentaAuth adminCuenta = cuentaAuthRepository.findByEmail("admin@nutritrack.com")
                .orElseThrow(() -> new RuntimeException("Usuario admin no encontrado"));
        CuentaAuth demoCuenta = cuentaAuthRepository.findByEmail("demo@nutritrack.com")
                .orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));

        PerfilUsuario adminPerfil = perfilUsuarioRepository.findByCuentaId(adminCuenta.getId())
                .orElseThrow(() -> new RuntimeException("Perfil admin no encontrado"));
        PerfilUsuario demoPerfil = perfilUsuarioRepository.findByCuentaId(demoCuenta.getId())
                .orElseThrow(() -> new RuntimeException("Perfil demo no encontrado"));

        createHealthProfile(adminPerfil, UsuarioPerfilSalud.ObjetivoSalud.MANTENER_FORMA,
                UsuarioPerfilSalud.NivelActividad.ALTO);
        createHealthProfile(demoPerfil, UsuarioPerfilSalud.ObjetivoSalud.PERDER_PESO,
                UsuarioPerfilSalud.NivelActividad.MODERADO);

        createMeasurements(adminPerfil, new double[][]{
                {70.0, 175},
                {70.2, 175},
                {70.1, 175},
                {70.3, 175},
                {70.2, 175},
                {70.4, 175},
                {70.3, 175},
                {70.5, 175},
                {70.4, 175},
                {70.5, 175},
                {70.5, 175}
        });

        createMeasurements(demoPerfil, new double[][]{
                {78.0, 168},
                {77.5, 168},
                {77.0, 168},
                {76.5, 168},
                {76.0, 168},
                {75.5, 168},
                {75.0, 168},
                {74.5, 168},
                {74.0, 168},
                {73.0, 168},
                {72.5, 168}
        });

        log.info("✅ Datos de demostración cargados:");
        log.info("   👔 Admin: 11 mediciones (70.0→70.5 kg, MANTENER_FORMA)");
        log.info("   👤 Demo: 11 mediciones (78.0→72.5 kg, PERDER_PESO, -5.5 kg)");
    }

    private void createHealthProfile(PerfilUsuario perfil,
                                     UsuarioPerfilSalud.ObjetivoSalud objetivo,
                                     UsuarioPerfilSalud.NivelActividad actividad) {
        UsuarioPerfilSalud perfilSalud = UsuarioPerfilSalud.builder()
                .perfilUsuario(perfil)
                .objetivoActual(objetivo)
                .nivelActividadActual(actividad)
                .fechaActualizacion(LocalDate.now())
                .build();
        usuarioPerfilSaludRepository.save(perfilSalud);
    }

    private void createMeasurements(PerfilUsuario perfil, double[][] data) {
        LocalDate startDate = LocalDate.of(2025, 9, 1);

        for (int i = 0; i < data.length; i++) {
            LocalDate fecha = startDate.plusWeeks(i);
            if (i == 9) fecha = LocalDate.of(2025, 11, 3);
            if (i == 10) fecha = LocalDate.of(2025, 11, 4);

            UsuarioHistorialMedidas medida = UsuarioHistorialMedidas.builder()
                    .perfilUsuario(perfil)
                    .fechaMedicion(fecha)
                    .peso(BigDecimal.valueOf(data[i][0]))
                    .altura(BigDecimal.valueOf(data[i][1]))
                    .build();
            usuarioHistorialMedidasRepository.save(medida);
        }
    }
}
