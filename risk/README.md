# CoreBanking - Risk Service

Servicio de evaluación de riesgo para la plataforma CoreBanking.

## Estado

✅ **Implementado** - Servicio completo y funcional.

## Funcionalidades

- Evaluación automática de riesgo para solicitudes de préstamo
- Cálculo de riskScore (0-100, donde >70 = alto riesgo)
- Determinación de riskLevel (LOW, MEDIUM, HIGH)
- Aplicación de reglas de negocio para evaluación de riesgo:
  - Credit Score del cliente
  - Relación deuda-ingresos (debt-to-income ratio)
  - Plazo del préstamo
  - Nivel de ingresos mensuales
  - Monto solicitado

## Arquitectura

Este servicio sigue el patrón de Arquitectura Hexagonal (Ports and Adapters) igual que los otros servicios del proyecto.

## Configuración

- **Puerto**: 8083 (dev), 8080 (prod)
- **Base de datos**: `risk_db`

## Endpoints

- `POST /api/v1/risk-assessments` - Evaluar riesgo de una solicitud de préstamo
- `GET /api/v1/risk-assessments/loan-application/{loanApplicationId}` - Obtener evaluación por ID de solicitud
- `GET /api/v1/risk-assessments/{id}` - Obtener evaluación por ID

## Compilación y Ejecución

```bash
# Compilar
cd risk
mvn clean install

# Ejecutar en desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Estructura

```
risk/
├── src/main/java/com/corebanking/risk_service/
│   ├── domain/
│   │   ├── model/
│   │   │   ├── RiskAssessment.java
│   │   │   └── RiskLevel.java
│   │   ├── port/
│   │   │   └── RiskAssessmentRepositoryPort.java
│   │   └── service/
│   │       └── RiskAssessmentService.java
│   ├── adapter/
│   │   ├── persistence/
│   │   │   ├── RiskAssessmentEntity.java
│   │   │   ├── RiskAssessmentJpaRepository.java
│   │   │   └── JpaRiskAssessmentRepositoryAdapter.java
│   │   └── rest/
│   │       └── RiskAssessmentController.java
│   ├── config/
│   │   ├── GlobalExceptionHandler.java
│   │   └── MessageConfig.java
│   └── RiskServiceApplication.java
└── pom.xml
```

