package com.corebanking.loan_service.adapter.rest;

import com.corebanking.loan_service.domain.model.LoanApplication;
import com.corebanking.loan_service.domain.model.LoanStatus;
import com.corebanking.loan_service.domain.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanApplicationController {

    private static final Logger log = LoggerFactory.getLogger(LoanApplicationController.class);

    private final LoanService loanService;
    private final MessageSource messageSource;

    public LoanApplicationController(LoanService loanService, MessageSource messageSource) {
        this.loanService = loanService;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<Object> createLoanApplication(
            @Valid @RequestBody CreateLoanApplicationRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        log.info("Received create loan application request: customerId={}, requestedAmount={}, termInMonths={}",
                request.customerId(), request.requestedAmount(), request.termInMonths());

        try {
            LoanApplication loanApplication = loanService.createLoanApplication(
                request.customerId(),
                request.requestedAmount(),
                request.termInMonths()
            );

            LoanApplicationResponse response = toResponse(loanApplication);

            log.info("Loan application created successfully with ID: {}", loanApplication.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Business validation failed: {}", e.getMessage());
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getLoanApplicationById(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        return loanService.getLoanApplicationById(id)
                .map(loan -> ResponseEntity.<Object>ok(toResponse(loan)))
                .orElseGet(() -> {
                    String message = messageSource.getMessage("loan.not.found", null, "Loan application not found", locale);
                    return ResponseEntity.<Object>status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(message));
                });
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanApplicationResponse>> getLoanApplicationsByCustomerId(
            @PathVariable UUID customerId) {

        List<LoanApplication> loans = loanService.getLoanApplicationsByCustomerId(customerId);
        List<LoanApplicationResponse> responses = loans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplicationResponse>> getLoanApplicationsByStatus(
            @PathVariable LoanStatus status) {

        List<LoanApplication> loans = loanService.getLoanApplicationsByStatus(status);
        List<LoanApplicationResponse> responses = loans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Object> approveLoanApplication(
            @PathVariable UUID id,
            @Valid @RequestBody ApproveLoanRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            LoanApplication loanApplication = loanService.approveLoanApplication(id, request.approvedBy());
            LoanApplicationResponse response = toResponse(loanApplication);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Object> rejectLoanApplication(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            LoanApplication loanApplication = loanService.rejectLoanApplication(id);
            LoanApplicationResponse response = toResponse(loanApplication);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    private LoanApplicationResponse toResponse(LoanApplication loan) {
        return new LoanApplicationResponse(
            loan.getId(),
            loan.getCustomerId(),
            loan.getRequestedAmount(),
            loan.getTermInMonths(),
            loan.getStatus().name(),
            loan.getCreatedAt(),
            loan.getApprovedAt(),
            loan.getApprovedBy()
        );
    }
}

// DTOs
record CreateLoanApplicationRequest(
        @NotNull(message = "Customer ID cannot be null")
        UUID customerId,
        
        @NotNull(message = "Requested amount cannot be null")
        @DecimalMin(value = "10000", message = "Requested amount must be at least 10000")
        @DecimalMax(value = "50000000", message = "Requested amount must be at most 50000000")
        BigDecimal requestedAmount,
        
        @NotNull(message = "Term in months cannot be null")
        @Min(value = 6, message = "Term in months must be at least 6")
        @Max(value = 60, message = "Term in months must be at most 60")
        Integer termInMonths
) {}

record ApproveLoanRequest(
        @NotBlank(message = "Approved by cannot be blank")
        @Size(max = 100, message = "Approved by must not exceed 100 characters")
        String approvedBy
) {}

record LoanApplicationResponse(
        UUID id,
        UUID customerId,
        BigDecimal requestedAmount,
        Integer termInMonths,
        String status,
        java.time.Instant createdAt,
        java.time.Instant approvedAt,
        String approvedBy
) {}

record ErrorResponse(String error) {}

