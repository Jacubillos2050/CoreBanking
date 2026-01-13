# CoreBanking - Auth Service

Servicio de autenticación y autorización para la plataforma CoreBanking, basado en arquitectura hexagonal (Hexagonal Architecture) y Spring Boot.

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso de la API](#uso-de-la-api)
- [Seguridad JWT](#seguridad-jwt)
- [Validaciones](#validaciones)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Testing](#testing)
- [Despliegue](#despliegue)

## 📝 Descripción

Este servicio proporciona funcionalidades de autenticación y autorización para la plataforma CoreBanking. Permite:

- Registro de usuarios con roles (CUSTOMER, ANALYST, ADMIN)
- Autenticación mediante JWT (JSON Web Tokens)
- Validación de credenciales
- Protección de endpoints mediante filtros de seguridad
- Validaciones de datos de entrada

## 🏗️ Arquitectura

El proyecto sigue los principios de **Arquitectura Hexagonal** (también conocida como Ports and Adapters):

```
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│  ┌──────────┐  ┌──────────────────┐   │
│  │  Model   │  │  Ports (Interfaces)│  │
│  └──────────┘  └──────────────────┘   │
│  ┌──────────────────────────────────┐  │
│  │      Services (Business Logic)   │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
           ↕ (Ports)
┌─────────────────────────────────────────┐
│         Adapter Layer                    │
│  ┌──────────┐  ┌──────────┐  ┌───────┐ │
│  │   REST   │  │  JPA     │  │  JWT  │ │
│  │ Adapters │  │ Adapters │  │Adapter│ │
│  └──────────┘  └──────────┘  └───────┘ │
└─────────────────────────────────────────┘
```

### Capas:

1. **Domain Layer**: Contiene la lógica de negocio, modelos y puertos (interfaces)
2. **Adapter Layer**: Implementaciones concretas (REST, JPA, Security)

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 4.0.1**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **JWT (jjwt 0.12.6)** - Tokens de autenticación
- **MariaDB** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias
- **Jakarta Validation** - Validaciones de datos

## 📦 Requisitos

- Java 17 o superior
- Maven 3.6+
- MariaDB 10.5+ (o MySQL 8.0+)
- Git

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd CoreBanking
```

### 2. Configurar la base de datos

Crear la base de datos en MariaDB:

```sql
CREATE DATABASE auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar variables de entorno

Para desarrollo, las configuraciones están en `application-dev.yml`. Para producción, configurar:

```bash
export JWT_SECRET="YourVeryStrongSecretKeyThatIsAtLeast32CharactersLong!"
export DB_USERNAME="your_db_username"
export DB_PASSWORD="your_db_password"
```

### 4. Compilar y ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar con perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# O ejecutar el JAR
java -jar target/auth-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## ⚙️ Configuración

### Perfiles de Spring

El proyecto incluye dos perfiles:

#### Desarrollo (`dev`)
- Puerto: `8081`
- Base de datos: `localhost:3306`
- DDL: `update` (crea/actualiza tablas automáticamente)
- Logging: `DEBUG`
- JWT expiration: 24 horas

#### Producción (`prod`)
- Puerto: `8080`
- Base de datos: Configurada mediante variables de entorno
- DDL: `validate` (solo valida esquema)
- Logging: `WARN`
- JWT expiration: 1 hora

### Configuración JWT

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}  # Mínimo 32 caracteres
    expiration-ms: 3600000  # 1 hora en milisegundos
```

**⚠️ Importante**: En producción, usar un secreto JWT fuerte (mínimo 32 caracteres) y almacenarlo como variable de entorno.

## 📡 Uso de la API

### Base URL

- Desarrollo: `http://localhost:8081`
- Producción: `http://your-domain:8080`

### Endpoints

#### 1. Registrar Usuario

```http
POST /api/v1/auth/register
Content-Type: application/json
Accept-Language: es (opcional)

{
  "username": "usuario123",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "username": "usuario123",
  "role": "CUSTOMER"
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "El usuario ya existe"
}
```

#### 2. Iniciar Sesión

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "usuario123",
  "password": "password123"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Respuesta de error (400 Bad Request):**
```json
{
  "error": "Invalid credentials"
}
```

#### 3. Endpoints Protegidos

Para acceder a endpoints protegidos, incluir el token JWT en el header:

```http
GET /api/v1/protected-endpoint
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Roles Disponibles

- `CUSTOMER` - Cliente de la plataforma
- `ANALYST` - Analista de préstamos
- `ADMIN` - Administrador del sistema

## 🔐 Seguridad JWT

### Implementación

El servicio utiliza **JWT (JSON Web Tokens)** para autenticación stateless:

1. **Generación de Token**: Al hacer login, se genera un JWT que contiene:
   - Username (subject)
   - Role (claim)
   - Fecha de emisión (issuedAt)
   - Fecha de expiración (expiration)

2. **Filtro de Autenticación**: `JwtAuthenticationFilter` intercepta todas las peticiones:
   - Extrae el token del header `Authorization: Bearer <token>`
   - Valida el token
   - Establece la autenticación en el contexto de Spring Security

3. **Configuración de Seguridad**: `SecurityConfig` configura:
   - Endpoints públicos: `/api/v1/auth/register`, `/api/v1/auth/login`
   - Resto de endpoints requieren autenticación
   - Sesiones stateless (sin estado)

### Corrección de SignatureAlgorithm Deprecado

Se ha corregido el uso de `SignatureAlgorithm.HS256` deprecado en jjwt 0.12.x. Ahora se usa directamente:

```java
.signWith(key)  // En lugar de .signWith(key, SignatureAlgorithm.HS256)
```

El algoritmo se infiere automáticamente del tipo de clave (HMAC-SHA256 para SecretKey).

## ✅ Validaciones

### UserEntity

La entidad `UserEntity` incluye las siguientes validaciones:

- **username**:
  - `@NotBlank` - No puede estar vacío
  - `@NotNull` - No puede ser null
  - `@Size(min=3, max=50)` - Entre 3 y 50 caracteres
  - `@Column(unique=true)` - Constraint único en base de datos

- **password**:
  - `@NotBlank` - No puede estar vacío
  - `@NotNull` - No puede ser null
  - `@Size(min=6)` - Mínimo 6 caracteres

- **role**:
  - `@NotBlank` - No puede estar vacío
  - `@NotNull` - No puede ser null
  - `@Size(max=20)` - Máximo 20 caracteres

### Constraint Único en Username

Se ha agregado un constraint único a nivel de base de datos:

```java
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username")
})
@Column(nullable = false, unique = true, length = 50)
```

Esto garantiza que no se puedan crear usuarios duplicados, tanto a nivel de aplicación como de base de datos.

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/corebanking/auth_service/
│   │       ├── adapter/              # Capa de adaptadores
│   │       │   ├── persistence/     # Adaptadores JPA
│   │       │   │   ├── UserEntity.java
│   │       │   │   ├── UserJpaRepository.java
│   │       │   │   └── JpaUserRepositoryAdapter.java
│   │       │   ├── rest/            # Adaptadores REST
│   │       │   │   ├── AuthController.java
│   │       │   │   ├── UserResponse.java
│   │       │   │   └── ErrorResponse.java
│   │       │   └── security/        # Adaptadores de seguridad
│   │       │       ├── JwtTokenProviderAdapter.java
│   │       │       └── JwtAuthenticationFilter.java
│   │       ├── config/              # Configuraciones
│   │       │   ├── SecurityConfig.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── MessageConfig.java
│   │       ├── domain/              # Capa de dominio
│   │       │   ├── model/          # Modelos de dominio
│   │       │   │   └── User.java
│   │       │   ├── port/           # Puertos (interfaces)
│   │       │   │   ├── UserRepositoryPort.java
│   │       │   │   └── JwtTokenProviderPort.java
│   │       │   └── service/        # Servicios de negocio
│   │       │       └── AuthService.java
│   │       └── AuthServiceApplication.java
│   └── resources/
│       ├── application.yml          # Configuración común
│       ├── application-dev.yml      # Configuración desarrollo
│       ├── application-prod.yml     # Configuración producción
│       └── messages/                # Mensajes i18n
│           ├── messages_es.properties
│           └── messages_en.properties
└── test/
    └── java/
        └── com/corebanking/auth_service/
            ├── AuthServiceApplicationTests.java
            └── domain/service/
                └── AuthServiceTest.java
```

## 🧪 Testing

### Ejecutar Tests

```bash
mvn test
```

### Tests Incluidos

- `AuthServiceTest` - Tests unitarios del servicio de autenticación
- `AuthServiceApplicationTests` - Tests de integración

## 🚢 Despliegue

### Docker (Recomendado)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### Variables de Entorno Requeridas (Producción)

```bash
JWT_SECRET=<secret-fuerte-mínimo-32-caracteres>
DB_USERNAME=<usuario-db>
DB_PASSWORD=<contraseña-db>
```

### Health Check

El servicio expone endpoints de salud (si se configura Spring Actuator):

```http
GET /actuator/health
```

## 🔧 Troubleshooting

### Error: "JWT signature does not match"

- Verificar que `JWT_SECRET` sea el mismo en todos los servicios
- Asegurarse de que el secreto tenga al menos 32 caracteres

### Error: "Username already exists"

- El username debe ser único. Verificar que no exista otro usuario con el mismo nombre.

### Error: "Invalid credentials"

- Verificar que el username y password sean correctos
- Asegurarse de que el usuario esté registrado

### Error de conexión a base de datos

- Verificar que MariaDB esté ejecutándose
- Verificar credenciales en `application-dev.yml` o variables de entorno
- Verificar que la base de datos `auth_db` exista

## 📄 Licencia

Este proyecto es parte de la plataforma CoreBanking.

## 👥 Contribución

Para contribuir al proyecto:

1. Crear una rama desde `main`
2. Realizar los cambios
3. Ejecutar tests
4. Crear un Pull Request

## 📞 Soporte

Para soporte o preguntas, contactar al equipo de desarrollo.

---

**Versión**: 0.0.1-SNAPSHOT  
**Última actualización**: 2024
