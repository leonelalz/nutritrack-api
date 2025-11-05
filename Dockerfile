# ============================================================================
# Dockerfile para NutriTrack API - Render Deployment
# ============================================================================

# Etapa 1: Build con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom.xml primero (para aprovechar cache de Docker)
COPY pom.xml .

# Descargar dependencias (esto se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación (sin tests para deployment rápido)
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime con JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar JAR desde etapa de build
COPY --from=build /app/target/nutritrack-API-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto (Render usa variable PORT)
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicio - Render pasa PORT como variable
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -Dserver.port=${PORT:-8080} -jar app.jar"]
