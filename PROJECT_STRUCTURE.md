# CoreBanking - Estructura del Proyecto

## Estructura de Microservicios

El proyecto CoreBanking está organizado como una arquitectura de microservicios, donde cada servicio es independiente y sigue el patrón de Arquitectura Hexagonal (Ports and Adapters).

```
CoreBanking/
├── auth-service/          # Servicio de autenticación y autorización
├── customer-service/      # Servicio de gestión de clientes
├── risk-service/          # Servicio de evaluación de riesgo
├── loan-service/          # Servicio de préstamos (pendiente)
├── audit-service/         # Servicio de auditoría
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

### 3. risk-service
- **Puerto**: 8083 (dev), 8080 (prod)
- **Base de datos**: `risk_db`
- **Funcionalidades**:
  - Evaluar riesgo automáticamente para solicitudes de préstamo
  - Calcular riskScore (0-100, donde >70 = alto riesgo)
  - Determinar riskLevel (LOW, MEDIUM, HIGH)
  - Aplicar reglas de negocio (credit score, debt-to-income, plazo, ingresos, monto)
  - Obtener evaluación por ID de solicitud o ID de evaluación

### 4. audit-service
- **Puerto**: 8084 (dev), 8080 (prod)
- **Base de datos**: `audit_db`
- **Funcionalidades**:
  - Registro de todas las operaciones críticas
  - Trazabilidad completa de cambios
  - Consultas por usuario, entidad, acción y rango de fechas
  - Captura automática de IP y User-Agent
  - Registro de detalles en formato JSON o texto

## Servicios Pendientes

### 5. loan-service
- Gestión de solicitudes de préstamo
- Integración con risk-service y customer-service

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
- `risk_db` - Para risk-service
- `loan_db` - Para loan-service (pendiente)
- `audit_db` - Para audit-service

## Configuración

Cada servicio tiene sus propios archivos de configuración:
- `application.yml` - Configuración común
- `application-dev.yml` - Configuración de desarrollo
- `application-prod.yml` - Configuración de producción

## Compilación y Ejecución

Cada servicio se compila y ejecuta de forma independiente:

```bash
# Compilar
cd audit
mvn clean install

# Ejecutar
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

