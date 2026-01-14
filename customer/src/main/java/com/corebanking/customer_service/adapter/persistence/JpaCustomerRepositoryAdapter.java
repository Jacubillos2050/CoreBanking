package com.corebanking.customer_service.adapter.persistence;

import com.corebanking.customer_service.domain.model.Customer;
import com.corebanking.customer_service.domain.port.CustomerRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpaRepository;

    public JpaCustomerRepositoryAdapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        CustomerEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Customer toDomain(CustomerEntity entity) {
        return new Customer(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getMonthlyIncome(),
            entity.getCreditScore()
        );
    }

    private CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        if (customer.getId() != null) {
            entity.setId(customer.getId());
        }
        entity.setName(customer.getName());
        entity.setEmail(customer.getEmail());
        entity.setMonthlyIncome(customer.getMonthlyIncome());
        entity.setCreditScore(customer.getCreditScore());
        return entity;
    }
}

