CREATE TABLE "cuentas_auth" (
  "id" UUID PRIMARY KEY,
  "email" VARCHAR(150) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "active" BOOL NOT NULL DEFAULT true,
  "id_rol" BIGINT NOT NULL,
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "roles" (
  "id" BIGINT PRIMARY KEY,
  "tipo_rol" VARCHAR(50) NOT NULL
);

CREATE TABLE "perfiles_usuario" (
  "id" UUID PRIMARY KEY,
  "id_usuario" UUID UNIQUE NOT NULL,
  "nombre" VARCHAR(255) NOT NULL,
  "unidades_medida" VARCHAR(10) DEFAULT 'KG',
  "fecha_inicio_app" DATE NOT NULL
);

CREATE TABLE "usuario_perfil_salud" (
  "id_perfil" UUID PRIMARY KEY,
  "objetivo_actual" VARCHAR(50),
  "nivel_actividad_actual" VARCHAR(50),
  "fecha_actualizacion" DATE NOT NULL
);

CREATE TABLE "usuario_historial_medidas" (
  "id" BIGINT PRIMARY KEY,
  "id_perfil" UUID NOT NULL,
  "fecha_medicion" DATE NOT NULL,
  "peso" DECIMAL(5,2),
  "altura" DECIMAL(5,2),
  "imc" DECIMAL(5,2),
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "ejercicios" (
  "id" BIGINT PRIMARY KEY,
  "nombre" VARCHAR(150) NOT NULL,
  "tipo_ejercicio" VARCHAR(50),
  "musculo_principal" VARCHAR(100),
  "duracion" INT,
  "dificultad" VARCHAR(50),
  "calorias_estimadas" DECIMAL(6,2),
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "comidas" (
  "id" BIGINT PRIMARY KEY,
  "nombre" VARCHAR(255) NOT NULL,
  "tipo_comida" VARCHAR(50),
  "tiempo_elaboracion" INT,
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "ingredientes" (
  "id" BIGINT PRIMARY KEY,
  "nombre" VARCHAR(255) NOT NULL,
  "grupo_alimenticio" VARCHAR(50),
  "energia" DECIMAL(5,2),
  "proteinas" DECIMAL(5,2),
  "grasas" DECIMAL(5,2),
  "carbohidratos" DECIMAL(5,2),
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "catalogo_metas" (
  "id" BIGINT PRIMARY KEY,
  "nombre" VARCHAR(255) NOT NULL,
  "descripcion" TEXT,
  "duracion_estimada_dias" INT,
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "catalogo_actividades" (
  "id" BIGINT PRIMARY KEY,
  "id_catalogo_meta" BIGINT NOT NULL,
  "nombre" VARCHAR(255) NOT NULL,
  "descripcion" TEXT,
  "tipo_actividad" VARCHAR(50) NOT NULL,
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "catalogo_planes_nutricion" (
  "id" BIGINT PRIMARY KEY,
  "id_catalogo_actividad" BIGINT UNIQUE NOT NULL,
  "nombre" VARCHAR(150),
  "tipo_plan" VARCHAR(50),
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "catalogo_rutinas" (
  "id_catalogo_actividad" BIGINT,
  "id_ejercicio" BIGINT,
  "repeticiones" INT,
  "series" INT,
  PRIMARY KEY ("id_catalogo_actividad", "id_ejercicio")
);

CREATE TABLE "catalogo_plan_comidas" (
  "id_catalogo_plan" BIGINT,
  "id_comida" BIGINT,
  PRIMARY KEY ("id_catalogo_plan", "id_comida")
);

CREATE TABLE "recetas" (
  "id_comida" BIGINT,
  "id_ingrediente" BIGINT,
  "cantidad_ingrediente" DECIMAL(5,2) NOT NULL,
  PRIMARY KEY ("id_comida", "id_ingrediente")
);

CREATE TABLE "etiquetas" (
  "id" BIGINT PRIMARY KEY,
  "nombre" VARCHAR(100) UNIQUE NOT NULL,
  "tipo_etiqueta" VARCHAR(50) NOT NULL,
  "descripcion" TEXT,
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "etiquetas_metas" (
  "id_catalogo_meta" BIGINT,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_catalogo_meta", "id_etiqueta")
);

CREATE TABLE "etiquetas_planes" (
  "id_catalogo_plan" BIGINT,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_catalogo_plan", "id_etiqueta")
);

CREATE TABLE "etiquetas_ingredientes" (
  "id_ingrediente" BIGINT,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_ingrediente", "id_etiqueta")
);

CREATE TABLE "etiquetas_ejercicios" (
  "id_ejercicio" BIGINT,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_ejercicio", "id_etiqueta")
);

CREATE TABLE "usuario_etiquetas_salud" (
  "id_perfil" UUID,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_perfil", "id_etiqueta")
);

CREATE TABLE "usuario_metas_asignadas" (
  "id" BIGINT PRIMARY KEY,
  "id_cliente" UUID NOT NULL,
  "id_catalogo_meta" BIGINT NOT NULL,
  "estado" VARCHAR(50) NOT NULL,
  "fecha_inicio_asignacion" DATE NOT NULL,
  "fecha_fin_asignacion" DATE,
  "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "usuario_actividades_progreso" (
  "id" BIGINT PRIMARY KEY,
  "id_meta_asignada" BIGINT NOT NULL,
  "id_catalogo_actividad" BIGINT NOT NULL,
  "actividad_acabada" BOOL NOT NULL DEFAULT false,
  "fecha_completado" DATE
);

COMMENT ON COLUMN "roles"."tipo_rol" IS 'Ej: Admin, Cliente';

COMMENT ON COLUMN "perfiles_usuario"."id_usuario" IS 'Vincula a cuentas_auth con su perfil';

COMMENT ON COLUMN "usuario_perfil_salud"."id_perfil" IS 'Relación 1-a-1 con perfiles_usuario.id';

COMMENT ON COLUMN "usuario_perfil_salud"."objetivo_actual" IS 'PERDER_PESO, GANAR_MASA_MUSCULAR, MANTENER_FORMA, etc.';

COMMENT ON COLUMN "usuario_perfil_salud"."nivel_actividad_actual" IS 'BAJO, MODERADO, ALTO';

COMMENT ON COLUMN "perfiles_usuario"."unidades_medida" IS 'KG o LBS para el sistema de medidas del usuario';

COMMENT ON COLUMN "usuario_historial_medidas"."altura" IS 'Puede ser nulo si no cambia';

COMMENT ON COLUMN "usuario_historial_medidas"."id_perfil" IS 'Referencia a perfiles_usuario.id';

COMMENT ON COLUMN "comidas"."tipo_comida" IS 'DESAYUNO, ALMUERZO, CENA, SNACK';

COMMENT ON COLUMN "comidas"."tiempo_elaboracion" IS 'Tiempo en minutos';

COMMENT ON COLUMN "catalogo_actividades"."tipo_actividad" IS 'EJERCICIO o NUTRICION';

COMMENT ON COLUMN "catalogo_planes_nutricion"."id_catalogo_actividad" IS 'Una actividad de nutrición ES un plan';

COMMENT ON COLUMN "etiquetas"."tipo_etiqueta" IS 'ALERGIA, CONDICION_MEDICA, OBJETIVO, DIETA, DIFICULTAD, GRUPO_MUSCULAR, TIPO_EJERCICIO';

COMMENT ON COLUMN "usuario_metas_asignadas"."id" IS 'ID único de esta asignación';

COMMENT ON COLUMN "usuario_metas_asignadas"."estado" IS 'ACTIVO, PAUSADO, COMPLETADO, CANCELADO';

COMMENT ON COLUMN "usuario_actividades_progreso"."id_meta_asignada" IS 'FK a usuario_metas_asignadas.id';

-- ================================================
-- FOREIGN KEYS
-- ================================================

ALTER TABLE "cuentas_auth" ADD FOREIGN KEY ("id_rol") REFERENCES "roles" ("id");

-- Relación correcta: PerfilUsuario pertenece a CuentaAuth
ALTER TABLE "perfiles_usuario" ADD FOREIGN KEY ("id_usuario") REFERENCES "cuentas_auth" ("id") ON DELETE CASCADE;

-- Relación correcta: UsuarioPerfilSalud extiende PerfilUsuario (mismo ID)
ALTER TABLE "usuario_perfil_salud" ADD FOREIGN KEY ("id_perfil") REFERENCES "perfiles_usuario" ("id") ON DELETE CASCADE;

ALTER TABLE "usuario_historial_medidas" ADD FOREIGN KEY ("id_perfil") REFERENCES "perfiles_usuario" ("id");

ALTER TABLE "usuario_etiquetas_salud" ADD FOREIGN KEY ("id_perfil") REFERENCES "perfiles_usuario" ("id");

ALTER TABLE "usuario_etiquetas_salud" ADD FOREIGN KEY ("id_etiqueta") REFERENCES "etiquetas" ("id");

ALTER TABLE "catalogo_actividades" ADD FOREIGN KEY ("id_catalogo_meta") REFERENCES "catalogo_metas" ("id");

ALTER TABLE "catalogo_actividades" ADD FOREIGN KEY ("id") REFERENCES "catalogo_planes_nutricion" ("id_catalogo_actividad");

ALTER TABLE "catalogo_rutinas" ADD FOREIGN KEY ("id_catalogo_actividad") REFERENCES "catalogo_actividades" ("id");

ALTER TABLE "catalogo_rutinas" ADD FOREIGN KEY ("id_ejercicio") REFERENCES "ejercicios" ("id");

ALTER TABLE "catalogo_plan_comidas" ADD FOREIGN KEY ("id_catalogo_plan") REFERENCES "catalogo_planes_nutricion" ("id");

ALTER TABLE "catalogo_plan_comidas" ADD FOREIGN KEY ("id_comida") REFERENCES "comidas" ("id");

ALTER TABLE "recetas" ADD FOREIGN KEY ("id_comida") REFERENCES "comidas" ("id");

ALTER TABLE "recetas" ADD FOREIGN KEY ("id_ingrediente") REFERENCES "ingredientes" ("id");

ALTER TABLE "etiquetas_metas" ADD FOREIGN KEY ("id_catalogo_meta") REFERENCES "catalogo_metas" ("id");

ALTER TABLE "etiquetas_metas" ADD FOREIGN KEY ("id_etiqueta") REFERENCES "etiquetas" ("id");

ALTER TABLE "etiquetas_planes" ADD FOREIGN KEY ("id_catalogo_plan") REFERENCES "catalogo_planes_nutricion" ("id");

ALTER TABLE "etiquetas_planes" ADD FOREIGN KEY ("id_etiqueta") REFERENCES "etiquetas" ("id");

ALTER TABLE "etiquetas_ingredientes" ADD FOREIGN KEY ("id_ingrediente") REFERENCES "ingredientes" ("id");

ALTER TABLE "etiquetas_ingredientes" ADD FOREIGN KEY ("id_etiqueta") REFERENCES "etiquetas" ("id");

ALTER TABLE "etiquetas_ejercicios" ADD FOREIGN KEY ("id_ejercicio") REFERENCES "ejercicios" ("id");

ALTER TABLE "etiquetas_ejercicios" ADD FOREIGN KEY ("id_etiqueta") REFERENCES "etiquetas" ("id");

ALTER TABLE "usuario_metas_asignadas" ADD FOREIGN KEY ("id_cliente") REFERENCES "perfiles_usuario" ("id");

ALTER TABLE "usuario_metas_asignadas" ADD FOREIGN KEY ("id_catalogo_meta") REFERENCES "catalogo_metas" ("id");

ALTER TABLE "usuario_actividades_progreso" ADD FOREIGN KEY ("id_meta_asignada") REFERENCES "usuario_metas_asignadas" ("id");

ALTER TABLE "usuario_actividades_progreso" ADD FOREIGN KEY ("id_catalogo_actividad") REFERENCES "catalogo_actividades" ("id");

-- ================================================
-- INDEXES FOR PERFORMANCE
-- ================================================

-- Índices para mejorar búsquedas frecuentes
CREATE INDEX idx_cuentas_email ON cuentas_auth(email);
CREATE INDEX idx_cuentas_active ON cuentas_auth(active);
CREATE INDEX idx_perfil_usuario ON perfiles_usuario(id_usuario);
CREATE INDEX idx_metas_asignadas_cliente ON usuario_metas_asignadas(id_cliente);
CREATE INDEX idx_metas_asignadas_estado ON usuario_metas_asignadas(estado);
CREATE INDEX idx_actividades_progreso_meta ON usuario_actividades_progreso(id_meta_asignada);
CREATE INDEX idx_historial_medidas_perfil ON usuario_historial_medidas(id_perfil);
CREATE INDEX idx_historial_medidas_fecha ON usuario_historial_medidas(fecha_medicion);
CREATE INDEX idx_etiquetas_tipo ON etiquetas(tipo_etiqueta);
CREATE INDEX idx_catalogo_actividades_meta ON catalogo_actividades(id_catalogo_meta);
CREATE INDEX idx_catalogo_actividades_tipo ON catalogo_actividades(tipo_actividad);

-- ================================================
-- SEQUENCES FOR AUTO-INCREMENT IDS
-- ================================================

CREATE SEQUENCE seq_roles START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_etiquetas START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_ingredientes START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_ejercicios START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_comidas START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_catalogo_metas START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_catalogo_actividades START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_catalogo_planes_nutricion START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_usuario_metas_asignadas START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_usuario_actividades_progreso START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_usuario_historial_medidas START WITH 1 INCREMENT BY 1;

-- ================================================
-- INSERT INITIAL DATA
-- ================================================

-- Roles iniciales
INSERT INTO roles (id, tipo_rol) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, tipo_rol) VALUES (2, 'ROLE_USER');

-- Etiquetas de ejemplo para Objetivos
INSERT INTO etiquetas (id, nombre, tipo_etiqueta, descripcion) VALUES 
(1, 'Perder Peso', 'OBJETIVO', 'Objetivo de pérdida de peso'),
(2, 'Ganar Músculo', 'OBJETIVO', 'Objetivo de ganancia muscular'),
(3, 'Mantener Forma', 'OBJETIVO', 'Mantener estado físico actual');

-- Etiquetas de ejemplo para Alergias
INSERT INTO etiquetas (id, nombre, tipo_etiqueta, descripcion) VALUES 
(10, 'Lácteos', 'ALERGIA', 'Alergia a productos lácteos'),
(11, 'Nueces', 'ALERGIA', 'Alergia a frutos secos'),
(12, 'Gluten', 'ALERGIA', 'Alergia o intolerancia al gluten'),
(13, 'Mariscos', 'ALERGIA', 'Alergia a mariscos');

-- Etiquetas de ejemplo para Condiciones Médicas
INSERT INTO etiquetas (id, nombre, tipo_etiqueta, descripcion) VALUES 
(20, 'Diabetes', 'CONDICION_MEDICA', 'Diabetes tipo 1 o 2'),
(21, 'Hipertensión', 'CONDICION_MEDICA', 'Presión arterial alta'),
(22, 'Colesterol Alto', 'CONDICION_MEDICA', 'Niveles altos de colesterol');

-- Etiquetas de ejemplo para Dificultad
INSERT INTO etiquetas (id, nombre, tipo_etiqueta, descripcion) VALUES 
(30, 'Principiante', 'DIFICULTAD', 'Nivel principiante'),
(31, 'Intermedio', 'DIFICULTAD', 'Nivel intermedio'),
(32, 'Avanzado', 'DIFICULTAD', 'Nivel avanzado');

-- ================================================
-- COMMENTS ON IMPROVEMENTS
-- ================================================

COMMENT ON TABLE usuario_etiquetas_salud IS 'Normalización de alergias y condiciones médicas del usuario usando etiquetas';
COMMENT ON TABLE etiquetas_ejercicios IS 'Relación N-N entre ejercicios y etiquetas para clasificación';
COMMENT ON COLUMN perfiles_usuario.unidades_medida IS 'Sistema de medidas preferido por el usuario (KG o LBS)';
COMMENT ON COLUMN catalogo_metas.created_at IS 'Timestamp de creación para auditoría';
COMMENT ON COLUMN catalogo_metas.updated_at IS 'Timestamp de última actualización';
