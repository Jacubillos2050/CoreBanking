package com.corebanking.risk_service.adapter.rest;

import com.corebanking.risk_service.domain.model.RiskAssessment;
import com.corebanking.risk_service.domain.service.RiskAssessmentService;
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
@RequestMapping("/api/v1/risk-assessments")
public class RiskAssessmentController {

    private static final Logger log = LoggerFactory.getLogger(RiskAssessmentController.class);

    private final RiskAssessmentService riskAssessmentService;
    private final MessageSource messageSource;

    public RiskAssessmentController(RiskAssessmentService riskAssessmentService, MessageSource messageSource) {
        this.riskAssessmentService = riskAssessmentService;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<Object> evaluateRisk(
            @Valid @RequestBody EvaluateRiskRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        log.info("Received risk evaluation request: loanApplicationId={}, creditScore={}, requestedAmount={}, termInMonths={}, monthlyIncome={}",
                request.loanApplicationId(), request.customerCreditScore(), request.requestedAmount(), 
                request.termInMonths(), request.monthlyIncome());

        try {
            RiskAssessment assessment = riskAssessmentService.evaluateRisk(
                request.loanApplicationId(),
                request.customerCreditScore(),
                request.requestedAmount(),
                request.termInMonths(),
                request.monthlyIncome()
            );

            RiskAssessmentResponse response = new RiskAssessmentResponse(
                assessment.getId(),
                assessment.getLoanApplicationId(),
                assessment.getRiskScore(),
                assessment.getRiskLevel().name(),
                assessment.getRulesApplied(),
                assessment.getEvaluatedAt()
            );

            log.info("Risk assessment created successfully with ID: {} for loan application: {}", 
                    assessment.getId(), assessment.getLoanApplicationId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Business validation failed: {}", e.getMessage());
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/loan-application/{loanApplicationId}")
    public ResponseEntity<Object> getByLoanApplicationId(
            @PathVariable UUID loanApplicationId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        return riskAssessmentService.getByLoanApplicationId(loanApplicationId)
                .map(assessment -> ResponseEntity.<Object>ok(new RiskAssessmentResponse(
                    assessment.getId(),
                    assessment.getLoanApplicationId(),
                    assessment.getRiskScore(),
                    assessment.getRiskLevel().name(),
                    assessment.getRulesApplied(),
                    assessment.getEvaluatedAt()
                )))
                .orElseGet(() -> {
                    String message = messageSource.getMessage("risk.assessment.not.found", null, 
                            "Risk assessment not found", locale);
                    return ResponseEntity.<Object>status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(message));
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        return riskAssessmentService.getById(id)
                .map(assessment -> ResponseEntity.<Object>ok(new RiskAssessmentResponse(
                    assessment.getId(),
                    assessment.getLoanApplicationId(),
                    assessment.getRiskScore(),
                    assessment.getRiskLevel().name(),
                    assessment.getRulesApplied(),
                    assessment.getEvaluatedAt()
                )))
                .orElseGet(() -> {
                    String message = messageSource.getMessage("risk.assessment.not.found", null, 
                            "Risk assessment not found", locale);
                    return ResponseEntity.<Object>status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(message));
                });
    }
}


