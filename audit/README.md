# Audit Service

Servicio de auditoría para la plataforma CoreBanking, basado en arquitectura hexagonal.

## Descripción

El servicio de auditoría registra todas las operaciones críticas realizadas en el sistema, proporcionando trazabilidad completa de las acciones de los usuarios.

## Funcionalidades

- **Registro de auditoría**: Registra operaciones con información detallada (usuario, acción, entidad, detalles, IP, user agent)
- **Consultas por usuario**: Obtener todos los registros de auditoría de un usuario específico
- **Consultas por entidad**: Obtener registros de auditoría por tipo de entidad o por entidad específica
- **Consultas por acción**: Filtrar registros por tipo de acción (CREATE, UPDATE, DELETE, etc.)
- **Consultas por rango de fechas**: Obtener registros dentro de un período de tiempo específico
- **Consultas combinadas**: Filtrar por usuario y rango de fechas simultáneamente

## Puerto

- **Desarrollo**: 8084
- **Producción**: 8080

## Base de Datos

- **Nombre**: `audit_db`
- **Motor**: MariaDB

## Estructura del Proyecto

```
audit/
├── src/main/java/com/corebanking/audit_service/
│   ├── domain/
│   │   ├── model/
│   │   │   └── AuditLog.java
│   │   ├── port/
│   │   │   └── AuditRepositoryPort.java
│   │   └── service/
│   │       └── AuditService.java
│   ├── adapter/
│   │   ├── persistence/
│   │   │   ├── AuditLogEntity.java
│   │   │   ├── AuditLogJpaRepository.java
│   │   │   └── JpaAuditRepositoryAdapter.java
│   │   └── rest/
│   │       └── AuditController.java
│   ├── config/
│   │   ├── GlobalExceptionHandler.java
│   │   └── MessageConfig.java
│   └── AuditServiceApplication.java
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    └── messages/
        ├── messages_en.properties
        └── messages_es.properties
```

## Modelo de Datos

### AuditLog

- `id`: UUID único del registro
- `userId`: ID del usuario que realizó la acción
- `action`: Tipo de acción (CREATE, UPDATE, DELETE, etc.)
- `entityType`: Tipo de entidad afectada (Customer, Loan, etc.)
- `entityId`: ID de la entidad afectada
- `details`: Detalles adicionales en formato JSON o texto
- `timestamp`: Fecha y hora de la acción
- `ipAddress`: Dirección IP del cliente
- `userAgent`: User agent del navegador/cliente

## API Endpoints

### POST /api/v1/audit
Crear un nuevo registro de auditoría.

**Request Body:**
```json
{
  "userId": "user-123",
  "action": "CREATE",
  "entityType": "Customer",
  "entityId": "customer-456",
  "details": "Customer created with email: john@example.com"
}
```

### GET /api/v1/audit/{id}
Obtener un registro de auditoría por ID.

### GET /api/v1/audit/user/{userId}
Obtener todos los registros de auditoría de un usuario.

### GET /api/v1/audit/entity/{entityType}
Obtener todos los registros de auditoría de un tipo de entidad.

### GET /api/v1/audit/entity/{entityType}/{entityId}
Obtener todos los registros de auditoría de una entidad específica.

### GET /api/v1/audit/action/{action}
Obtener todos los registros de auditoría de una acción específica.

### GET /api/v1/audit/date-range?start={start}&end={end}
Obtener registros de auditoría en un rango de fechas.

**Parámetros:**
- `start`: Fecha de inicio (ISO 8601)
- `end`: Fecha de fin (ISO 8601)

### GET /api/v1/audit/user/{userId}/date-range?start={start}&end={end}
Obtener registros de auditoría de un usuario en un rango de fechas.

## Compilación y Ejecución

```bash
# Compilar
cd audit
mvn clean install

# Ejecutar en modo desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Ejemplo de Uso

### Crear un registro de auditoría

```bash
curl -X POST http://localhost:8084/api/v1/audit \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-123",
    "action": "CREATE",
    "entityType": "Customer",
    "entityId": "customer-456",
    "details": "Customer created successfully"
  }'
```

### Consultar registros por usuario

```bash
curl http://localhost:8084/api/v1/audit/user/user-123
```

### Consultar registros por rango de fechas

```bash
curl "http://localhost:8084/api/v1/audit/date-range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59"
```

## Notas

- El servicio captura automáticamente la IP del cliente y el User-Agent desde los headers HTTP
- Los registros de auditoría son inmutables (solo lectura después de creados)
- Se recomienda usar índices en la base de datos para optimizar las consultas (ya incluidos en la entidad)

