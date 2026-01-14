package com.corebanking.customer_service.adapter.rest;

import com.corebanking.customer_service.domain.model.Customer;
import com.corebanking.customer_service.domain.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;
    private final MessageSource messageSource;

    public CustomerController(CustomerService customerService, MessageSource messageSource) {
        this.customerService = customerService;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        log.info("Received create customer request: name={}, email={}, monthlyIncome={}, creditScore={}",
                request.name(), request.email(), request.monthlyIncome(), request.creditScore());

        try {
            Customer customer = customerService.createCustomer(
                request.name(),
                request.email(),
                request.monthlyIncome(),
                request.creditScore()
            );

            CustomerResponse response = new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getMonthlyIncome(),
                customer.getCreditScore()
            );

            log.info("Customer created successfully with ID: {}", customer.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Business validation failed: {}", e.getMessage());
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.<Object>ok(new CustomerResponse(
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getMonthlyIncome(),
                    customer.getCreditScore()
                )))
                .orElseGet(() -> {
                    String message = messageSource.getMessage("customer.not.found", null, "Customer not found", locale);
                    return ResponseEntity.<Object>status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(message));
                });
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getCustomerByEmail(
            @PathVariable String email,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        return customerService.getCustomerByEmail(email)
                .map(customer -> ResponseEntity.<Object>ok(new CustomerResponse(
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getMonthlyIncome(),
                    customer.getCreditScore()
                )))
                .orElseGet(() -> {
                    String message = messageSource.getMessage("customer.not.found", null, "Customer not found", locale);
                    return ResponseEntity.<Object>status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(message));
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            Customer customer = customerService.updateCustomer(
                id,
                request.name(),
                request.monthlyIncome(),
                request.creditScore()
            );

            CustomerResponse response = new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getMonthlyIncome(),
                customer.getCreditScore()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCustomer(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            customerService.deleteCustomer(id);
            String message = messageSource.getMessage("customer.deleted", null, "Customer deleted successfully", locale);
            return ResponseEntity.ok(new ErrorResponse(message));
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }
}

// DTOs
record CreateCustomerRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,
        
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,
        
        @NotNull(message = "Monthly income cannot be null")
        @DecimalMin(value = "0.01", message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,
        
        @NotNull(message = "Credit score cannot be null")
        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 850, message = "Credit score must be at most 850")
        Integer creditScore
) {}

record UpdateCustomerRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,
        
        @DecimalMin(value = "0.01", message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,
        
        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 850, message = "Credit score must be at most 850")
        Integer creditScore
) {}

record CustomerResponse(
        UUID id,
        String name,
        String email,
        BigDecimal monthlyIncome,
        Integer creditScore
) {}

record ErrorResponse(String error) {}

