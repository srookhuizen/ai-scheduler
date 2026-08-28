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
    public CustomerDTO save(Customer customer) {
        return customerMapper.toDto(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO save(CustomerDTO dto) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(dto.getEmail());
        if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customerMapper.map(dto, customer);
            return customerMapper.toDto(customerRepository.save(customer));
        } else {
            Customer entity = customerMapper.toEntity(dto);
            return customerMapper.toDto(customerRepository.save(entity));
        }
    }

    public Optional<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email).map(customerMapper::toDto);
    }
}
