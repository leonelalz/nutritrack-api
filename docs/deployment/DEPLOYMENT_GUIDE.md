# Guía de Despliegue 🚀

**Versión:** 1.0  
**Última actualización:** Octubre 2025

## 📋 Tabla de Contenidos

- [Ambientes](#ambientes)
- [Pre-requisitos](#pre-requisitos)
- [Configuración](#configuración)
- [Despliegue Local](#despliegue-local)
- [Despliegue en Desarrollo](#despliegue-en-desarrollo)
- [Despliegue en Producción](#despliegue-en-producción)
- [Docker](#docker)
- [Monitoreo](#monitoreo)
- [Rollback](#rollback)
- [Troubleshooting](#troubleshooting)

## 🌍 Ambientes

| Ambiente | Propósito | URL | Base de Datos |
|----------|-----------|-----|---------------|
| **Local** | Desarrollo individual | http://localhost:8080 | MySQL local |
| **Development** | Integración y pruebas | https://dev-api.nutritrack.com | MySQL Dev |
| **Staging** | Pre-producción | https://staging-api.nutritrack.com | MySQL Staging |
| **Production** | Producción | https://api.nutritrack.com | MySQL Prod (Cluster) |

## 📦 Pre-requisitos

### Software Requerido

- **Java JDK 17+**
- **Maven 3.8+**
- **MySQL 8.0+** o **PostgreSQL 13+**
- **Git**
- **Docker** (opcional, recomendado)

### Accesos Necesarios

- [ ] Repositorio GitHub
- [ ] Base de datos del ambiente
- [ ] Servidor de aplicaciones
- [ ] Variables de entorno/secrets
- [ ] Logs y monitoreo

## ⚙️ Configuración

### Variables de Entorno

Crear archivo de configuración por ambiente:

#### application-local.properties

```properties
# Server
server.port=8080
spring.application.name=nutritrack-api

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/nutritrack_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=local-secret-key-for-development-only
jwt.expiration=86400000

# Logging
logging.level.com.nutritrack=DEBUG
logging.level.org.springframework=INFO
```

#### application-dev.properties

```properties
# Server
server.port=8080

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Logging
logging.level.com.nutritrack=INFO
logging.file.name=/var/log/nutritrack/application.log
```

#### application-prod.properties

```properties
# Server
server.port=8080

# Database (Read Replica)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Logging
logging.level.com.nutritrack=WARN
logging.file.name=/var/log/nutritrack/application.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

## 💻 Despliegue Local

### Paso 1: Configurar Base de Datos

```bash
# Iniciar MySQL con Docker
docker run --name nutritrack-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=nutritrack_db \
  -p 3306:3306 \
  -d mysql:8.0

# O crear manualmente
mysql -u root -p
CREATE DATABASE nutritrack_db;
CREATE USER 'nutritrack_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON nutritrack_db.* TO 'nutritrack_user'@'localhost';
FLUSH PRIVILEGES;
```

### Paso 2: Clonar y Configurar

```bash
# Clonar repositorio
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api

# Crear archivo de configuración local
cp src/main/resources/application.properties.example \
   src/main/resources/application-local.properties

# Editar con tus credenciales
nano src/main/resources/application-local.properties
```

### Paso 3: Compilar y Ejecutar

```bash
# Compilar
./mvnw clean package -DskipTests

# Ejecutar con perfil local
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# O ejecutar JAR directamente
java -jar target/nutritrack-api-1.0.0.jar --spring.profiles.active=local
```

### Paso 4: Verificar

```bash
# Verificar health check
curl http://localhost:8080/actuator/health

# Respuesta esperada:
# {"status":"UP"}
```

## 🔧 Despliegue en Desarrollo

### Usando Maven

```bash
# 1. Actualizar código
git checkout development
git pull origin development

# 2. Compilar con tests
./mvnw clean package

# 3. Detener servicio actual
sudo systemctl stop nutritrack-api

# 4. Respaldar versión anterior
cp /opt/nutritrack/nutritrack-api.jar \
   /opt/nutritrack/backup/nutritrack-api-$(date +%Y%m%d).jar

# 5. Copiar nuevo JAR
cp target/nutritrack-api-1.0.0.jar /opt/nutritrack/nutritrack-api.jar

# 6. Iniciar servicio
sudo systemctl start nutritrack-api

# 7. Verificar logs
sudo journalctl -u nutritrack-api -f
```

### Usando Systemd Service

Crear archivo `/etc/systemd/system/nutritrack-api.service`:

```ini
[Unit]
Description=NutriTrack API Service
After=syslog.target network.target mysql.service

[Service]
Type=simple
User=nutritrack
WorkingDirectory=/opt/nutritrack
ExecStart=/usr/bin/java -jar \
  -Dspring.profiles.active=dev \
  -Xms512m -Xmx1024m \
  /opt/nutritrack/nutritrack-api.jar

SuccessExitStatus=143
StandardOutput=journal
StandardError=journal
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Comandos de gestión:

```bash
# Habilitar servicio
sudo systemctl enable nutritrack-api

# Iniciar servicio
sudo systemctl start nutritrack-api

# Ver estado
sudo systemctl status nutritrack-api

# Ver logs
sudo journalctl -u nutritrack-api -f

# Reiniciar
sudo systemctl restart nutritrack-api

# Detener
sudo systemctl stop nutritrack-api
```

## 🌐 Despliegue en Producción

### Checklist Pre-Despliegue

- [ ] Tests pasan (unit + integration)
- [ ] Code review aprobado
- [ ] Migraciones de DB preparadas
- [ ] Variables de entorno configuradas
- [ ] Backup de base de datos realizado
- [ ] Plan de rollback preparado
- [ ] Notificación a stakeholders
- [ ] Ventana de mantenimiento agendada

### Proceso de Despliegue

#### 1. Preparación

```bash
# Crear tag de release
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# Backup de base de datos
mysqldump -u root -p nutritrack_db > backup_$(date +%Y%m%d_%H%M%S).sql
```

#### 2. Build

```bash
# Compilar con perfil de producción
./mvnw clean package -Pprod -DskipTests=false

# Verificar JAR creado
ls -lh target/nutritrack-api-1.0.0.jar
```

#### 3. Migraciones de Base de Datos

```bash
# Ejecutar migraciones (Flyway)
./mvnw flyway:migrate -Dflyway.url=${DB_URL} \
  -Dflyway.user=${DB_USER} \
  -Dflyway.password=${DB_PASS}

# Verificar estado
./mvnw flyway:info
```

#### 4. Despliegue Blue-Green

```bash
# Servidor Green (nuevo)
# 1. Copiar JAR al servidor Green
scp target/nutritrack-api-1.0.0.jar \
  user@green-server:/opt/nutritrack/

# 2. Iniciar en Green
ssh user@green-server
sudo systemctl start nutritrack-api
curl http://localhost:8080/actuator/health

# 3. Actualizar Load Balancer (cambiar tráfico a Green)
# Usar AWS ELB, Nginx, etc.

# 4. Monitorear métricas y logs

# 5. Si todo OK, detener Blue (viejo)
ssh user@blue-server
sudo systemctl stop nutritrack-api
```

### Configuración Nginx (Reverse Proxy)

```nginx
upstream nutritrack_backend {
    server 10.0.1.10:8080; # Servidor 1
    server 10.0.1.11:8080; # Servidor 2
    server 10.0.1.12:8080; # Servidor 3
}

server {
    listen 80;
    server_name api.nutritrack.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.nutritrack.com;
    
    ssl_certificate /etc/ssl/certs/nutritrack.crt;
    ssl_certificate_key /etc/ssl/private/nutritrack.key;
    
    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    # Proxy settings
    location / {
        proxy_pass http://nutritrack_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # Health check endpoint
    location /actuator/health {
        proxy_pass http://nutritrack_backend;
        access_log off;
    }
}
```

## 🐳 Docker

### Dockerfile

```dockerfile
# Multi-stage build
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar JAR desde build stage
COPY --from=build /app/target/nutritrack-api-*.jar app.jar

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: nutritrack-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: nutritrack_db
      MYSQL_USER: ${DB_USERNAME}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - nutritrack-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  api:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: nutritrack-api
    environment:
      SPRING_PROFILES_ACTIVE: ${ENV:-dev}
      DB_URL: jdbc:mysql://mysql:3306/nutritrack_db
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - nutritrack-network
    restart: unless-stopped

volumes:
  mysql_data:

networks:
  nutritrack-network:
    driver: bridge
```

### Comandos Docker

```bash
# Construir imagen
docker build -t nutritrack-api:1.0.0 .

# Ejecutar con docker-compose
docker-compose up -d

# Ver logs
docker-compose logs -f api

# Detener
docker-compose down

# Reconstruir y ejecutar
docker-compose up -d --build
```

## 📊 Monitoreo

### Spring Boot Actuator

Endpoints de monitoreo:

```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

Endpoints disponibles:
- `GET /actuator/health` - Estado de salud
- `GET /actuator/info` - Información de la app
- `GET /actuator/metrics` - Métricas
- `GET /actuator/prometheus` - Métricas para Prometheus

### Logs

Configuración de logging:

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/nutritrack/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/nutritrack/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### Métricas Importantes

- **CPU Usage** - < 70%
- **Memory Usage** - < 80%
- **Response Time** - < 500ms (p95)
- **Error Rate** - < 1%
- **Throughput** - Requests/second
- **Database Connections** - Pool usage

## 🔄 Rollback

### Procedimiento de Rollback

```bash
# 1. Identificar versión anterior
ls -lh /opt/nutritrack/backup/

# 2. Detener servicio actual
sudo systemctl stop nutritrack-api

# 3. Restaurar JAR anterior
cp /opt/nutritrack/backup/nutritrack-api-20251030.jar \
   /opt/nutritrack/nutritrack-api.jar

# 4. Rollback de base de datos (si es necesario)
mysql -u root -p nutritrack_db < backup_20251030.sql

# 5. Iniciar servicio
sudo systemctl start nutritrack-api

# 6. Verificar
curl http://localhost:8080/actuator/health

# 7. Actualizar Load Balancer si es necesario
```

### Estrategia de Rollback Automático

```bash
#!/bin/bash
# rollback.sh

BACKUP_DIR="/opt/nutritrack/backup"
CURRENT_JAR="/opt/nutritrack/nutritrack-api.jar"
SERVICE_NAME="nutritrack-api"

echo "Iniciando rollback..."

# Listar backups disponibles
echo "Backups disponibles:"
ls -lh $BACKUP_DIR

# Seleccionar backup
read -p "Ingrese nombre del archivo de backup: " BACKUP_FILE

# Detener servicio
sudo systemctl stop $SERVICE_NAME

# Restaurar JAR
cp $BACKUP_DIR/$BACKUP_FILE $CURRENT_JAR

# Iniciar servicio
sudo systemctl start $SERVICE_NAME

# Verificar
sleep 5
curl -f http://localhost:8080/actuator/health

if [ $? -eq 0 ]; then
    echo "✅ Rollback exitoso"
else
    echo "❌ Rollback falló - verificar logs"
    sudo journalctl -u $SERVICE_NAME -n 50
fi
```

## 🔍 Troubleshooting

### Aplicación no Inicia

```bash
# Verificar logs
sudo journalctl -u nutritrack-api -n 100

# Verificar puerto
sudo netstat -tlnp | grep 8080

# Verificar proceso Java
ps aux | grep java

# Verificar variables de entorno
sudo systemctl show nutritrack-api | grep Environment
```

### Errores de Base de Datos

```bash
# Verificar conectividad
mysql -h DB_HOST -u DB_USER -p

# Verificar pool de conexiones
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active

# Verificar logs de queries lentas
mysql> SHOW FULL PROCESSLIST;
```

### Performance Issues

```bash
# Thread dump
jstack <PID> > thread_dump.txt

# Heap dump
jmap -dump:live,format=b,file=heap_dump.hprof <PID>

# GC logs (añadir al JAVA_OPTS)
-Xlog:gc*:file=/var/log/nutritrack/gc.log
```

### Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `Connection refused` | DB no disponible | Verificar MySQL iniciado |
| `Out of memory` | Heap pequeño | Aumentar Xmx |
| `JWT signature invalid` | Secret incorrecto | Verificar JWT_SECRET |
| `Port already in use` | Otro proceso en 8080 | Cambiar puerto o matar proceso |

## 📝 Checklist Post-Despliegue

- [ ] Aplicación iniciada correctamente
- [ ] Health check responde OK
- [ ] Logs sin errores críticos
- [ ] Métricas dentro de rangos normales
- [ ] Tests de humo pasados
- [ ] Endpoints principales funcionando
- [ ] Notificar a equipo
- [ ] Actualizar documentación
- [ ] Cerrar ventana de mantenimiento

---

**Siguiente:** [MONITORING_GUIDE.md](MONITORING_GUIDE.md)  
**Soporte:** Contactar al equipo DevOps
