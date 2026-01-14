package com.corebanking.customer_service.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private String email;
    private BigDecimal monthlyIncome;
    private Integer creditScore;

    public Customer(UUID id, String name, String email, BigDecimal monthlyIncome, Integer creditScore) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.monthlyIncome = monthlyIncome;
        this.creditScore = creditScore;
    }

    public Customer(String name, String email, BigDecimal monthlyIncome, Integer creditScore) {
        this(null, name, email, monthlyIncome, creditScore);
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public Integer getCreditScore() { return creditScore; }
}

