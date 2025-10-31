# Inicio Rápido - NutriTrack API 🚀

Guía rápida para poner en marcha el proyecto en 5 minutos.

## ⚡ Requisitos Previos

- ✅ Java 17+
- ✅ Maven 3.8+
- ✅ MySQL 8.0+ (o Docker)

## 🚀 Instalación Rápida

### 1. Clonar el Repositorio

```bash
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api
```

### 2. Configurar Base de Datos

**Opción A: Con Docker (Recomendado)**

```bash
docker run --name nutritrack-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=nutritrack_db \
  -p 3306:3306 \
  -d mysql:8.0
```

**Opción B: MySQL Local**

```bash
mysql -u root -p
CREATE DATABASE nutritrack_db;
```

### 3. Configurar Aplicación

Crear archivo `src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nutritrack_db
spring.datasource.username=root
spring.datasource.password=root
jwt.secret=mi-secreto-local-para-desarrollo
```

### 4. Ejecutar

```bash
# Compilar y ejecutar
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

**¡Listo!** La API está en: `http://localhost:8080`

## 🧪 Verificar Instalación

```bash
# Health check
curl http://localhost:8080/actuator/health

# Respuesta esperada:
# {"status":"UP"}
```

## 📚 Próximos Pasos

1. **Leer documentación:** [README.md](../README.md)
2. **Guía de contribución:** [CONTRIBUTING.md](../CONTRIBUTING.md)
3. **Arquitectura:** [docs/architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md)
4. **Tu módulo:** Ver [docs/modules/](modules/)

## 🆘 Problemas Comunes

### Error: Port 8080 already in use

```bash
# Cambiar puerto en application-local.properties
server.port=8081
```

### Error: Cannot connect to MySQL

```bash
# Verificar MySQL iniciado
docker ps  # Para Docker
sudo systemctl status mysql  # Para instalación local
```

### Error: JWT secret not configured

```bash
# Añadir en application-local.properties
jwt.secret=cualquier-clave-secreta-de-al-menos-32-caracteres
```

## 💡 Tips

- **Ver logs detallados:** Cambiar nivel a DEBUG en properties
- **Recargar cambios:** Usar Spring DevTools
- **Tests:** `./mvnw test`
- **Limpiar build:** `./mvnw clean`

## 📞 Ayuda

¿Tienes problemas? Contacta al equipo:
- Slack: #nutritrack-dev
- Email: team@nutritrack.com
- Issues: [GitHub Issues](https://github.com/leonelalz/nutritrack-api/issues)

---

**¡Feliz desarrollo! 🎉**
