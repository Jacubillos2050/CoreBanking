# CoreBanking - Estructura del Proyecto

## Estructura de Microservicios

El proyecto CoreBanking está organizado como una arquitectura de microservicios, donde cada servicio es independiente y sigue el patrón de Arquitectura Hexagonal (Ports and Adapters).

```
CoreBanking/
├── auth-service/          # Servicio de autenticación y autorización
├── customer-service/      # Servicio de gestión de clientes
├── risk-service/          # Servicio de evaluación de riesgo (pendiente)
├── loan-service/          # Servicio de préstamos (pendiente)
├── audit-service/         # Servicio de auditoría (pendiente)
└── frontend-react/        # Frontend en React (pendiente)
```

## Arquitectura Hexagonal

Cada microservicio sigue el patrón de Arquitectura Hexagonal:

```
src/main/java/com/corebanking/{service_name}/
├── domain/                 # Núcleo del dominio (puro Java, sin Spring)
│   ├── model/             # Modelos de dominio
│   ├── port/              # Puertos (interfaces)
│   └── service/           # Servicios de negocio
├── adapter/                # Adaptadores (REST, DB, etc.)
│   ├── rest/              # Adaptadores REST
│   ├── persistence/       # Adaptadores de persistencia
│   └── security/          # Adaptadores de seguridad (solo auth-service)
├── config/                 # Configuraciones
└── {Service}Application.java
```

## Servicios Implementados

### 1. auth-service
- **Puerto**: 8081 (dev), 8080 (prod)
- **Base de datos**: `auth_db`
- **Funcionalidades**:
  - Registro de usuarios
  - Autenticación con JWT
  - Gestión de roles (CUSTOMER, ANALYST, ADMIN)

### 2. customer-service
- **Puerto**: 8082 (dev), 8080 (prod)
- **Base de datos**: `customer_db`
- **Funcionalidades**:
  - Crear cliente
  - Obtener cliente por ID o email
  - Actualizar cliente
  - Eliminar cliente
  - Validación de datos (email único, creditScore 300-850, etc.)

## Servicios Pendientes

### 3. risk-service
- Evaluación automática de riesgo para solicitudes de préstamo
- Cálculo de riskScore y riskLevel

### 4. loan-service
- Gestión de solicitudes de préstamo
- Integración con risk-service y customer-service

### 5. audit-service
- Registro de todas las operaciones críticas
- Trazabilidad de cambios

### 6. frontend-react
- Interfaz de usuario en React
- Consumo de APIs de los microservicios

## Notas de Migración

**Estado actual**: El código de `auth-service` está en la raíz del proyecto (`src/`).

**Para reorganizar**:
1. Mover el contenido de `src/` a `auth-service/src/`
2. Mover `pom.xml` a `auth-service/pom.xml`
3. Crear un `pom.xml` padre (opcional) para gestionar todos los servicios

## Base de Datos

Cada servicio tiene su propia base de datos:
- `auth_db` - Para auth-service
- `customer_db` - Para customer-service
- `risk_db` - Para risk-service (pendiente)
- `loan_db` - Para loan-service (pendiente)
- `audit_db` - Para audit-service (pendiente)

## Configuración

Cada servicio tiene sus propios archivos de configuración:
- `application.yml` - Configuración común
- `application-dev.yml` - Configuración de desarrollo
- `application-prod.yml` - Configuración de producción

## Compilación y Ejecución

Cada servicio se compila y ejecuta de forma independiente:

```bash
# Compilar
cd customer-service
mvn clean install

# Ejecutar
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

