package com.corebanking.risk_service.adapter.persistence;

import com.corebanking.risk_service.domain.model.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "risk_assessments", indexes = {
    @Index(name = "idx_loan_application_id", columnList = "loan_application_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "loan_application_id", nullable = false, unique = true)
    @NotNull(message = "Loan application ID cannot be null")
    private UUID loanApplicationId;
    
    @Column(name = "risk_score", nullable = false)
    @NotNull(message = "Risk score cannot be null")
    private Integer riskScore;
    
    @Column(name = "risk_level", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Risk level cannot be null")
    private RiskLevel riskLevel;
    
    @ElementCollection
    @CollectionTable(name = "risk_rules_applied", joinColumns = @JoinColumn(name = "risk_assessment_id"))
    @Column(name = "rule")
    private List<String> rulesApplied;
    
    @Column(name = "evaluated_at", nullable = false)
    @NotNull(message = "Evaluated at cannot be null")
    private Instant evaluatedAt;
}

