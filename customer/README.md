# CoreBanking - Customer Service

Servicio de gestión de clientes para la plataforma CoreBanking, basado en arquitectura hexagonal (Hexagonal Architecture) y Spring Boot.

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso de la API](#uso-de-la-api)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Testing](#testing)

## 📝 Descripción

Este servicio proporciona funcionalidades de gestión de clientes para la plataforma CoreBanking. Permite:

- Crear nuevos clientes
- Obtener información de clientes por ID o email
- Actualizar información de clientes
- Eliminar clientes
- Validaciones de datos (email único, creditScore 300-850, etc.)

## 🏗️ Arquitectura

El proyecto sigue los principios de **Arquitectura Hexagonal** (también conocida como Ports and Adapters):

```
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│  ┌──────────┐  ┌──────────────────┐   │
│  │ Customer │  │ CustomerRepository│   │
│  │  Model   │  │     Port          │   │
│  └──────────┘  └──────────────────┘   │
│  ┌──────────────────────────────────┐  │
│  │    CustomerService (Business)    │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
           ↕ (Ports)
┌─────────────────────────────────────────┐
│         Adapter Layer                    │
│  ┌──────────┐  ┌──────────┐            │
│  │   REST   │  │  JPA     │            │
│  │ Adapters │  │ Adapters │            │
│  └──────────┘  └──────────┘            │
└─────────────────────────────────────────┘
```

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 4.0.1**
- **Spring Data JPA** - Persistencia de datos
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

### 1. Configurar la base de datos

Crear la base de datos en MariaDB:

```sql
CREATE DATABASE customer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar variables de entorno

Para desarrollo, las configuraciones están en `application-dev.yml`. Para producción, configurar:

```bash
export DB_USERNAME="your_db_username"
export DB_PASSWORD="your_db_password"
```

### 3. Compilar y ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar con perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# O ejecutar el JAR
java -jar target/customer-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## ⚙️ Configuración

### Perfiles de Spring

#### Desarrollo (`dev`)
- Puerto: `8082`
- Base de datos: `localhost:3306`
- DDL: `update` (crea/actualiza tablas automáticamente)
- Logging: `DEBUG`

#### Producción (`prod`)
- Puerto: `8080`
- Base de datos: Configurada mediante variables de entorno
- DDL: `validate` (solo valida esquema)
- Logging: `WARN`

## 📡 Uso de la API

### Base URL

- Desarrollo: `http://localhost:8082`
- Producción: `http://your-domain:8080`

### Endpoints

#### 1. Crear Cliente

```http
POST /api/v1/customers
Content-Type: application/json
Accept-Language: es (opcional)

{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "monthlyIncome": 5000.00,
  "creditScore": 750
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "monthlyIncome": 5000.00,
  "creditScore": 750
}
```

#### 2. Obtener Cliente por ID

```http
GET /api/v1/customers/{id}
Accept-Language: es (opcional)
```

#### 3. Obtener Cliente por Email

```http
GET /api/v1/customers/email/{email}
Accept-Language: es (opcional)
```

#### 4. Actualizar Cliente

```http
PUT /api/v1/customers/{id}
Content-Type: application/json
Accept-Language: es (opcional)

{
  "name": "Juan Pérez García",
  "monthlyIncome": 6000.00,
  "creditScore": 780
}
```

#### 5. Eliminar Cliente

```http
DELETE /api/v1/customers/{id}
Accept-Language: es (opcional)
```

## ✅ Validaciones

### CustomerEntity

La entidad `CustomerEntity` incluye las siguientes validaciones:

- **name**:
  - `@NotBlank` - No puede estar vacío
  - `@Size(min=2, max=100)` - Entre 2 y 100 caracteres

- **email**:
  - `@NotBlank` - No puede estar vacío
  - `@Email` - Debe ser un email válido
  - `@Column(unique=true)` - Constraint único en base de datos

- **monthlyIncome**:
  - `@NotNull` - No puede ser null
  - Debe ser mayor que 0

- **creditScore**:
  - `@NotNull` - No puede ser null
  - Rango válido: 300-850

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/corebanking/customer_service/
│   │       ├── adapter/              # Capa de adaptadores
│   │       │   ├── persistence/     # Adaptadores JPA
│   │       │   │   ├── CustomerEntity.java
│   │       │   │   ├── CustomerJpaRepository.java
│   │       │   │   └── JpaCustomerRepositoryAdapter.java
│   │       │   └── rest/            # Adaptadores REST
│   │       │       └── CustomerController.java
│   │       ├── config/              # Configuraciones
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── MessageConfig.java
│   │       ├── domain/              # Capa de dominio
│   │       │   ├── model/          # Modelos de dominio
│   │       │   │   └── Customer.java
│   │       │   ├── port/           # Puertos (interfaces)
│   │       │   │   └── CustomerRepositoryPort.java
│   │       │   └── service/        # Servicios de negocio
│   │       │       └── CustomerService.java
│   │       └── CustomerServiceApplication.java
│   └── resources/
│       ├── application.yml          # Configuración común
│       ├── application-dev.yml      # Configuración desarrollo
│       ├── application-prod.yml     # Configuración producción
│       └── messages/                # Mensajes i18n
│           ├── messages_es.properties
│           └── messages_en.properties
└── test/
    └── java/
        └── com/corebanking/customer_service/
```

## 🧪 Testing

### Ejecutar Tests

```bash
mvn test
```

## 🔧 Troubleshooting

### Error: "Email already registered"

- El email debe ser único. Verificar que no exista otro cliente con el mismo email.

### Error: "Credit score must be between 300 and 850"

- El creditScore debe estar en el rango válido (300-850).

### Error de conexión a base de datos

- Verificar que MariaDB esté ejecutándose
- Verificar credenciales en `application-dev.yml` o variables de entorno
- Verificar que la base de datos `customer_db` exista

---

**Versión**: 0.0.1-SNAPSHOT  
**Última actualización**: 2024

