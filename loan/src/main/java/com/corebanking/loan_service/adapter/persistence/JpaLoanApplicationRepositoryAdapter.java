package com.corebanking.loan_service.adapter.persistence;

import com.corebanking.loan_service.domain.model.LoanApplication;
import com.corebanking.loan_service.domain.model.LoanStatus;
import com.corebanking.loan_service.domain.port.LoanApplicationRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaLoanApplicationRepositoryAdapter implements LoanApplicationRepositoryPort {

    private final LoanApplicationJpaRepository jpaRepository;

    public JpaLoanApplicationRepositoryAdapter(LoanApplicationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<LoanApplication> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<LoanApplication> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanApplication> findByStatus(LoanStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public LoanApplication save(LoanApplication loanApplication) {
        LoanApplicationEntity entity = toEntity(loanApplication);
        LoanApplicationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private LoanApplication toDomain(LoanApplicationEntity entity) {
        return new LoanApplication(
            entity.getId(),
            entity.getCustomerId(),
            entity.getRequestedAmount(),
            entity.getTermInMonths(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getApprovedAt(),
            entity.getApprovedBy()
        );
    }

    private LoanApplicationEntity toEntity(LoanApplication loanApplication) {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        if (loanApplication.getId() != null) {
            entity.setId(loanApplication.getId());
        }
        entity.setCustomerId(loanApplication.getCustomerId());
        entity.setRequestedAmount(loanApplication.getRequestedAmount());
        entity.setTermInMonths(loanApplication.getTermInMonths());
        entity.setStatus(loanApplication.getStatus());
        entity.setCreatedAt(loanApplication.getCreatedAt());
        entity.setApprovedAt(loanApplication.getApprovedAt());
        entity.setApprovedBy(loanApplication.getApprovedBy());
        return entity;
    }
}

