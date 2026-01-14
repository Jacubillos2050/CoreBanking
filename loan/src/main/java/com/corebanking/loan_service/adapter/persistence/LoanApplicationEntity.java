package com.corebanking.loan_service.adapter.persistence;

import com.corebanking.loan_service.domain.model.LoanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull(message = "Customer ID cannot be null")
    private UUID customerId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Requested amount cannot be null")
    private BigDecimal requestedAmount;
    
    @Column(nullable = false)
    @NotNull(message = "Term in months cannot be null")
    private Integer termInMonths;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Status cannot be null")
    private LoanStatus status;
    
    @Column(nullable = false, updatable = false)
    @NotNull(message = "Created at cannot be null")
    private Instant createdAt;
    
    @Column(nullable = true)
    private Instant approvedAt;
    
    @Column(nullable = true, length = 100)
    private String approvedBy;
}

