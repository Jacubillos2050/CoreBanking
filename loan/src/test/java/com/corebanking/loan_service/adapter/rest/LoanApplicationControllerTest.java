package com.corebanking.loan_service.adapter.rest;

import com.corebanking.loan_service.domain.model.LoanApplication;
import com.corebanking.loan_service.domain.model.LoanStatus;
import com.corebanking.loan_service.domain.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID id = UUID.randomUUID();
    private UUID customerId = UUID.randomUUID();
    private BigDecimal amount = new BigDecimal("50000");
    private Integer term = 24;

    @Test
    void testCreateLoanApplicationSuccess() throws Exception {
        LoanApplication loan = new LoanApplication(id, customerId, amount, term, LoanStatus.PENDING, Instant.now(), null, null);
        when(loanService.createLoanApplication(customerId, amount, term)).thenReturn(loan);

        String requestJson = """
            {
                "customerId": "%s",
                "requestedAmount": 50000,
                "termInMonths": 24
            }
            """.formatted(customerId);

        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.requestedAmount").value(50000))
                .andExpect(jsonPath("$.termInMonths").value(24))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(loanService).createLoanApplication(customerId, amount, term);
    }

    @Test
    void testCreateLoanApplicationValidationError() throws Exception {
        String requestJson = """
            {
                "customerId": "%s",
                "requestedAmount": 5000,
                "termInMonths": 24
            }
            """.formatted(customerId);

        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).createLoanApplication(any(), any(), any());
    }

    @Test
    void testCreateLoanApplicationBusinessError() throws Exception {
        when(loanService.createLoanApplication(customerId, amount, term))
            .thenThrow(new IllegalArgumentException("loan.amount.too.low"));
        when(messageSource.getMessage(eq("loan.amount.too.low"), eq(null), eq("Unknown error"), any(Locale.class))).thenReturn("Amount too low");

        String requestJson = """
            {
                "customerId": "%s",
                "requestedAmount": 50000,
                "termInMonths": 24
            }
            """.formatted(customerId);

        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en")
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown error"));

        verify(loanService).createLoanApplication(customerId, amount, term);
    }

    @Test
    void testGetLoanApplicationByIdFound() throws Exception {
        LoanApplication loan = new LoanApplication(id, customerId, amount, term, LoanStatus.PENDING, Instant.now(), null, null);
        when(loanService.getLoanApplicationById(id)).thenReturn(Optional.of(loan));

        mockMvc.perform(get("/api/v1/loans/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(loanService).getLoanApplicationById(id);
    }

    @Test
    void testGetLoanApplicationByIdNotFound() throws Exception {
        when(loanService.getLoanApplicationById(id)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("loan.not.found"), eq(null), eq("Loan application not found"), any(Locale.class))).thenReturn("Loan application not found");

        mockMvc.perform(get("/api/v1/loans/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Loan application not found"));

        verify(loanService).getLoanApplicationById(id);
    }

    @Test
    void testGetLoanApplicationsByCustomerId() throws Exception {
        List<LoanApplication> loans = List.of(
            new LoanApplication(id, customerId, amount, term, LoanStatus.PENDING, Instant.now(), null, null)
        );
        when(loanService.getLoanApplicationsByCustomerId(customerId)).thenReturn(loans);

        mockMvc.perform(get("/api/v1/loans/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()));

        verify(loanService).getLoanApplicationsByCustomerId(customerId);
    }

    @Test
    void testGetLoanApplicationsByStatus() throws Exception {
        List<LoanApplication> loans = List.of(
            new LoanApplication(id, customerId, amount, term, LoanStatus.APPROVED, Instant.now(), Instant.now(), "approver")
        );
        when(loanService.getLoanApplicationsByStatus(LoanStatus.APPROVED)).thenReturn(loans);

        mockMvc.perform(get("/api/v1/loans/status/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(loanService).getLoanApplicationsByStatus(LoanStatus.APPROVED);
    }

    @Test
    void testApproveLoanApplicationSuccess() throws Exception {
        LoanApplication loan = new LoanApplication(id, customerId, amount, term, LoanStatus.APPROVED, Instant.now(), Instant.now(), "approver@example.com");
        when(loanService.approveLoanApplication(id, "approver@example.com")).thenReturn(loan);

        String requestJson = """
            {
                "approvedBy": "approver@example.com"
            }
            """;

        mockMvc.perform(put("/api/v1/loans/{id}/approve", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.approvedBy").value("approver@example.com"));

        verify(loanService).approveLoanApplication(id, "approver@example.com");
    }

    @Test
    void testApproveLoanApplicationBusinessError() throws Exception {
        when(loanService.approveLoanApplication(id, "approver@example.com"))
            .thenThrow(new IllegalStateException("loan.status.not.pending"));
        when(messageSource.getMessage(eq("loan.status.not.pending"), eq(null), eq("Unknown error"), any(Locale.class))).thenReturn("Loan not pending");

        String requestJson = """
            {
                "approvedBy": "approver@example.com"
            }
            """;

        mockMvc.perform(put("/api/v1/loans/{id}/approve", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en")
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown error"));

        verify(loanService).approveLoanApplication(id, "approver@example.com");
    }

    @Test
    void testApproveLoanApplicationUnexpectedError() throws Exception {
        when(loanService.approveLoanApplication(id, "approver@example.com"))
            .thenThrow(new RuntimeException("Unexpected"));

        String requestJson = """
            {
                "approvedBy": "approver@example.com"
            }
            """;

        mockMvc.perform(put("/api/v1/loans/{id}/approve", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));

        verify(loanService).approveLoanApplication(id, "approver@example.com");
    }

    @Test
    void testRejectLoanApplicationSuccess() throws Exception {
        LoanApplication loan = new LoanApplication(id, customerId, amount, term, LoanStatus.REJECTED, Instant.now(), null, null);
        when(loanService.rejectLoanApplication(id)).thenReturn(loan);

        mockMvc.perform(put("/api/v1/loans/{id}/reject", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(loanService).rejectLoanApplication(id);
    }

    @Test
    void testRejectLoanApplicationBusinessError() throws Exception {
        when(loanService.rejectLoanApplication(id))
            .thenThrow(new IllegalStateException("loan.status.not.pending"));
        when(messageSource.getMessage(eq("loan.status.not.pending"), eq(null), eq("Unknown error"), any(Locale.class))).thenReturn("Loan not pending");

        mockMvc.perform(put("/api/v1/loans/{id}/reject", id)
                .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown error"));

        verify(loanService).rejectLoanApplication(id);
    }
}