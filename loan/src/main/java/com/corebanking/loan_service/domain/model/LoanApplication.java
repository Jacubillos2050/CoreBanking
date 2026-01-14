package com.corebanking.loan_service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class LoanApplication {
    private UUID id;
    private UUID customerId;
    private BigDecimal requestedAmount;
    private Integer termInMonths;
    private LoanStatus status;
    private Instant createdAt;
    private Instant approvedAt;
    private String approvedBy;

    public LoanApplication(UUID id, UUID customerId, BigDecimal requestedAmount, 
                          Integer termInMonths, LoanStatus status, Instant createdAt,
                          Instant approvedAt, String approvedBy) {
        this.id = id;
        this.customerId = customerId;
        this.requestedAmount = requestedAmount;
        this.termInMonths = termInMonths;
        this.status = status;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
    }

    public LoanApplication(UUID customerId, BigDecimal requestedAmount, 
                          Integer termInMonths) {
        this(null, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, 
             Instant.now(), null, null);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public Integer getTermInMonths() { return termInMonths; }
    public LoanStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getApprovedBy() { return approvedBy; }

    // Business methods
    public void approve(String approvedBy) {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("loan.status.not.pending");
        }
        this.status = LoanStatus.APPROVED;
        this.approvedAt = Instant.now();
        this.approvedBy = approvedBy;
    }

    public void reject() {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("loan.status.not.pending");
        }
        this.status = LoanStatus.REJECTED;
    }
}

