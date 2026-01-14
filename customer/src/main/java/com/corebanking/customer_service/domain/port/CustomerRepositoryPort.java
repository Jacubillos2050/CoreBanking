package com.corebanking.customer_service.domain.port;

import com.corebanking.customer_service.domain.model.Customer;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String email);
    Customer save(Customer customer);
    boolean existsByEmail(String email);
    void deleteById(UUID id);
}

