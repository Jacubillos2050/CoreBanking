package com.corebanking.risk_service.adapter.rest;

import com.corebanking.risk_service.domain.model.RiskAssessment;
import com.corebanking.risk_service.domain.model.RiskLevel;
import com.corebanking.risk_service.domain.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentControllerTest {

    @Mock
    private RiskAssessmentService riskAssessmentService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RiskAssessmentController controller;

    @Test
    void evaluateRisk_Success() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        RiskAssessment assessment = new RiskAssessment(
            assessmentId,
            loanApplicationId,
            45,
            RiskLevel.MEDIUM,
            List.of("CREDIT_SCORE_BELOW_700", "DEBT_TO_INCOME_MEDIUM"),
            Instant.now()
        );

        when(riskAssessmentService.evaluateRisk(
            loanApplicationId, 650, new BigDecimal("500000"), 24, new BigDecimal("3000")
        )).thenReturn(assessment);

        var request = new EvaluateRiskRequest(
            loanApplicationId, 650, new BigDecimal("500000"), 24, new BigDecimal("3000")
        );

        // When
        ResponseEntity<Object> result = controller.evaluateRisk(request, "en");

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof RiskAssessmentResponse);
        var response = (RiskAssessmentResponse) result.getBody();
        assertEquals(assessmentId, response.id());
        assertEquals(loanApplicationId, response.loanApplicationId());
        assertEquals(45, response.riskScore());
        assertEquals("MEDIUM", response.riskLevel());
        assertEquals(2, response.rulesApplied().size());
        verify(riskAssessmentService).evaluateRisk(loanApplicationId, 650, new BigDecimal("500000"), 24, new BigDecimal("3000"));
    }

    @Test
    void evaluateRisk_WithAcceptLanguageNull() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        RiskAssessment assessment = new RiskAssessment(
            assessmentId,
            loanApplicationId,
            45,
            RiskLevel.MEDIUM,
            List.of("CREDIT_SCORE_BELOW_700"),
            Instant.now()
        );

        when(riskAssessmentService.evaluateRisk(
            loanApplicationId, 650, new BigDecimal("500000"), 24, new BigDecimal("3000")
        )).thenReturn(assessment);

        var request = new EvaluateRiskRequest(
            loanApplicationId, 650, new BigDecimal("500000"), 24, new BigDecimal("3000")
        );

        // When
        ResponseEntity<Object> result = controller.evaluateRisk(request, null);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(riskAssessmentService).evaluateRisk(loanApplicationId, 650, new BigDecimal("500000"), 24, new BigDecimal("3000"));
    }

    @Test
    void evaluateRisk_BusinessValidationError() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();
        IllegalArgumentException exception = new IllegalArgumentException("INVALID_CREDIT_SCORE");

        when(riskAssessmentService.evaluateRisk(
            loanApplicationId, 200, new BigDecimal("500000"), 24, new BigDecimal("3000")
        )).thenThrow(exception);

        when(messageSource.getMessage("INVALID_CREDIT_SCORE", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("Invalid credit score provided");

        var request = new EvaluateRiskRequest(
            loanApplicationId, 200, new BigDecimal("500000"), 24, new BigDecimal("3000")
        );

        // When
        ResponseEntity<Object> result = controller.evaluateRisk(request, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        var error = (ErrorResponse) result.getBody();
        assertEquals("Invalid credit score provided", error.error());
        verify(messageSource).getMessage("INVALID_CREDIT_SCORE", null, "Unknown error", Locale.ENGLISH);
    }

    @Test
    void getByLoanApplicationId_Found() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        RiskAssessment assessment = new RiskAssessment(
            assessmentId,
            loanApplicationId,
            25,
            RiskLevel.LOW,
            List.of("CREDIT_SCORE_ACCEPTABLE"),
            Instant.now()
        );

        when(riskAssessmentService.getByLoanApplicationId(loanApplicationId))
            .thenReturn(Optional.of(assessment));

        // When
        ResponseEntity<Object> result = controller.getByLoanApplicationId(loanApplicationId, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof RiskAssessmentResponse);
        var response = (RiskAssessmentResponse) result.getBody();
        assertEquals(assessmentId, response.id());
        assertEquals(loanApplicationId, response.loanApplicationId());
        assertEquals(25, response.riskScore());
        assertEquals("LOW", response.riskLevel());
        verify(riskAssessmentService).getByLoanApplicationId(loanApplicationId);
    }

    @Test
    void getByLoanApplicationId_NotFound() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();

        when(riskAssessmentService.getByLoanApplicationId(loanApplicationId))
            .thenReturn(Optional.empty());

        when(messageSource.getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH))
            .thenReturn("Risk assessment not found");

        // When
        ResponseEntity<Object> result = controller.getByLoanApplicationId(loanApplicationId, "en");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        var error = (ErrorResponse) result.getBody();
        assertEquals("Risk assessment not found", error.error());
        verify(messageSource).getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH);
    }

    @Test
    void getByLoanApplicationId_WithAcceptLanguageNull() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();

        when(riskAssessmentService.getByLoanApplicationId(loanApplicationId))
            .thenReturn(Optional.empty());

        when(messageSource.getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH))
            .thenReturn("Risk assessment not found");

        // When
        ResponseEntity<Object> result = controller.getByLoanApplicationId(loanApplicationId, null);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(messageSource).getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH);
    }

    @Test
    void getById_Found() {
        // Given
        UUID assessmentId = UUID.randomUUID();
        UUID loanApplicationId = UUID.randomUUID();
        RiskAssessment assessment = new RiskAssessment(
            assessmentId,
            loanApplicationId,
            75,
            RiskLevel.HIGH,
            List.of("CREDIT_SCORE_BELOW_600", "DEBT_TO_INCOME_HIGH"),
            Instant.now()
        );

        when(riskAssessmentService.getById(assessmentId))
            .thenReturn(Optional.of(assessment));

        // When
        ResponseEntity<Object> result = controller.getById(assessmentId, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof RiskAssessmentResponse);
        var response = (RiskAssessmentResponse) result.getBody();
        assertEquals(assessmentId, response.id());
        assertEquals(loanApplicationId, response.loanApplicationId());
        assertEquals(75, response.riskScore());
        assertEquals("HIGH", response.riskLevel());
        verify(riskAssessmentService).getById(assessmentId);
    }

    @Test
    void getById_NotFound() {
        // Given
        UUID assessmentId = UUID.randomUUID();

        when(riskAssessmentService.getById(assessmentId))
            .thenReturn(Optional.empty());

        when(messageSource.getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH))
            .thenReturn("Risk assessment not found");

        // When
        ResponseEntity<Object> result = controller.getById(assessmentId, "en");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        var error = (ErrorResponse) result.getBody();
        assertEquals("Risk assessment not found", error.error());
        verify(messageSource).getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH);
    }

    @Test
    void getById_WithAcceptLanguageNull() {
        // Given
        UUID assessmentId = UUID.randomUUID();

        when(riskAssessmentService.getById(assessmentId))
            .thenReturn(Optional.empty());

        when(messageSource.getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH))
            .thenReturn("Risk assessment not found");

        // When
        ResponseEntity<Object> result = controller.getById(assessmentId, null);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(messageSource).getMessage("risk.assessment.not.found", null,
            "Risk assessment not found", Locale.ENGLISH);
    }
}