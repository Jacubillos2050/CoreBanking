# CoreBanking – Domain Model

## Entidades Principales

### 1. Customer (Cliente)
Representa a una persona física que solicita un préstamo.

- **id**: UUID (identificador único)
- **name**: String (nombre completo)
- **email**: String (único, validado)
- **monthlyIncome**: BigDecimal (ingresos mensuales en USD/EUR)
- **creditScore**: Integer (puntaje crediticio, rango 300–850)

> Nota: Un `Customer` está vinculado a un `User` con rol `CUSTOMER`.

---

### 2. User (Usuario del sistema)
Credenciales y roles para acceder a la plataforma.

- **username**: String (único, ej. email o nombre de usuario)
- **passwordHash**: String (hash seguro, ej. BCrypt)
- **role**: Enum { CUSTOMER, ANALYST, ADMIN }

> Solo los usuarios con rol `CUSTOMER` pueden crear `LoanApplication`.

---

### 3. LoanApplication (Solicitud de Préstamo)
Registro de una solicitud pendiente de evaluación.

- **id**: UUID
- **customerId**: UUID (referencia al cliente solicitante)
- **requestedAmount**: BigDecimal (monto solicitado)
- **termInMonths**: Integer (plazo en meses, ej. 12, 24, 36)
- **status**: Enum { PENDING, APPROVED, REJECTED }
- **createdAt**: Instant (fecha/hora UTC)
- **approvedAt**: Instant (opcional, solo si se aprueba)
- **approvedBy**: String (username del analista que aprobó)

> El estado inicial siempre es `PENDING`.

---

### 4. RiskAssessment (Evaluación de Riesgo)
Resultado del análisis automático de riesgo para una solicitud.

- **loanApplicationId**: UUID
- **riskScore**: Integer (0–100, donde >70 = alto riesgo)
- **riskLevel**: Enum { LOW, MEDIUM, HIGH }
- **rulesApplied**: List<String> (ej. ["INCOME_TOO_LOW", "CREDIT_SCORE_BELOW_600"])
- **evaluatedAt**: Instant

> Generado automáticamente al crear una `LoanApplication`.

---

### 5. AuditLog (Registro de Auditoría)
Pista inmutable de todas las operaciones relevantes.

- **id**: UUID
- **serviceName**: String (ej. "loan-service")
- **action**: String (ej. "LOAN_CREATED", "LOAN_APPROVED")
- **performedBy**: String (username o "SYSTEM")
- **timestamp**: Instant
- **payload**: JSON (datos relevantes en formato serializable)

> Ejemplo de payload:
> ```json
> { "loanId": "abc123", "oldStatus": "PENDING", "newStatus": "APPROVED" }
> ```

---

## Reglas de Negocio Clave

1. **Solo clientes registrados** pueden solicitar préstamos.
2. **Monto mínimo**: $10,000; **máximo**: $50,000,000.
3. **Plazo permitido**: 6 a 60 meses.
4. **Riesgo alto** → requiere revisión manual adicional (en este MVP, aún pasa a analista).
5. **Ninguna operación crítica** debe quedar sin registro en `AuditLog`.
6. **Internacionalización**: todos los mensajes de error/exito deben estar en `messages_es.properties` y `messages_en.properties`.
