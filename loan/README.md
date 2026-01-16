# CoreBanking - Loan Service

Servicio de gestión de préstamos para la plataforma CoreBanking.

## Estado

✅ **Implementado** - Servicio completo siguiendo arquitectura hexagonal.

## Funcionalidades

- ✅ Crear solicitudes de préstamo
- ✅ Obtener solicitud por ID
- ✅ Obtener solicitudes por cliente
- ✅ Obtener solicitudes por estado (PENDING, APPROVED, REJECTED)
- ✅ Aprobar préstamos (requiere usuario aprobador)
- ✅ Rechazar préstamos
- ✅ Validación de reglas de negocio (monto mínimo $10,000, máximo $50,000,000, plazo 6-60 meses)

## Arquitectura

Este servicio sigue el mismo patrón de Arquitectura Hexagonal que los otros servicios del proyecto:

```
loan/
├── domain/
│   ├── model/          # LoanApplication, LoanStatus
│   ├── port/           # LoanApplicationRepositoryPort
│   └── service/        # LoanService
├── adapter/
│   ├── persistence/    # Entity, JpaRepository, Adapter
│   └── rest/           # LoanApplicationController
├── config/             # GlobalExceptionHandler, MessageConfig
└── LoanServiceApplication.java
```

## Configuración

- **Puerto**: 8084 (dev), 8080 (prod)
- **Base de datos**: `loan_db`
- **Perfil activo por defecto**: `dev`

## Endpoints

- `POST /api/v1/loans` - Crear solicitud de préstamo
- `GET /api/v1/loans/{id}` - Obtener solicitud por ID
- `GET /api/v1/loans/customer/{customerId}` - Obtener solicitudes por cliente
- `GET /api/v1/loans/status/{status}` - Obtener solicitudes por estado
- `PUT /api/v1/loans/{id}/approve` - Aprobar préstamo
- `PUT /api/v1/loans/{id}/reject` - Rechazar préstamo

## Reglas de Negocio

1. **Monto mínimo**: $10,000
2. **Monto máximo**: $50,000,000
3. **Plazo permitido**: 6 a 60 meses
4. **Estado inicial**: Siempre PENDING
5. **Aprobación/Rechazo**: Solo se puede realizar si el estado es PENDING

## Internacionalización

El servicio soporta mensajes en español e inglés mediante el header `Accept-Language`.
