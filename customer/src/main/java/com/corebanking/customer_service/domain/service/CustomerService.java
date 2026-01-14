package com.corebanking.customer_service.domain.service;

import com.corebanking.customer_service.domain.model.Customer;
import com.corebanking.customer_service.domain.port.CustomerRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepositoryPort customerRepository;

    public CustomerService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(String name, String email, BigDecimal monthlyIncome, Integer creditScore) {
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("customer.email.exists");
        }
        
        // Validar rango de creditScore
        if (creditScore < 300 || creditScore > 850) {
            throw new IllegalArgumentException("customer.creditScore.invalid");
        }
        
        // Validar que monthlyIncome sea positivo
        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("customer.monthlyIncome.invalid");
        }
        
        Customer customer = new Customer(name, email, monthlyIncome, creditScore);
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer updateCustomer(UUID id, String name, BigDecimal monthlyIncome, Integer creditScore) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("customer.not.found"));
        
        // Validar rango de creditScore si se proporciona
        if (creditScore != null && (creditScore < 300 || creditScore > 850)) {
            throw new IllegalArgumentException("customer.creditScore.invalid");
        }
        
        // Validar que monthlyIncome sea positivo si se proporciona
        if (monthlyIncome != null && monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("customer.monthlyIncome.invalid");
        }
        
        // Crear nuevo objeto Customer con los datos actualizados
        Customer updatedCustomer = new Customer(
            id,
            name != null ? name : customer.getName(),
            customer.getEmail(), // Email no se puede cambiar
            monthlyIncome != null ? monthlyIncome : customer.getMonthlyIncome(),
            creditScore != null ? creditScore : customer.getCreditScore()
        );
        
        return customerRepository.save(updatedCustomer);
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("customer.not.found");
        }
        customerRepository.deleteById(id);
    }
}

