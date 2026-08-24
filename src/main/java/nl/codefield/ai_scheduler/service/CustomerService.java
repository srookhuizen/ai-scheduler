package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.mapper.CustomerMapper;
import nl.codefield.ai_scheduler.model.Customer;
import nl.codefield.ai_scheduler.repository.CustomerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer save(CustomerDTO dto) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(dto.getEmail());
        return optionalCustomer.orElseGet(() -> customerRepository.save(customerMapper.toEntity(dto)));
    }

    @Cacheable(value = "customers", key = "#email", unless = "#result == null || !#result.isPresent()")
    public Optional<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email).map(customerMapper::toDto);
    }
}
