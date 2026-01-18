package com.corebanking.customer_service.adapter.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "customers", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Monthly income cannot be null")
    private BigDecimal monthlyIncome;
    
    @Column(nullable = false)
    @NotNull(message = "Credit score cannot be null")
    private Integer creditScore;

    // Getters and setters (in case Lombok doesn't generate them)
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public Integer getCreditScore() { return creditScore; }
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
}

