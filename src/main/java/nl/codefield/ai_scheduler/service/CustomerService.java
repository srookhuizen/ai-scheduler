package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.exception.ResourceNotFoundException;
import nl.codefield.ai_scheduler.mapper.CustomerMapper;
import nl.codefield.ai_scheduler.entity.Customer;
import nl.codefield.ai_scheduler.repository.CompanyRepository;
import nl.codefield.ai_scheduler.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CompanyRepository companyRepository;

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public CustomerDTO save(Customer customer) {
        return customerMapper.map(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO save(CustomerDTO dto) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(dto.getEmail());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customerMapper.map(dto, customer);
            // update of the users profile, preferred company
            if (dto.getProfile() != null && dto.getProfile().getCompany() != null) {
                String id = dto.getProfile().getCompany().getId();
                if (id != null) {
                    // set it first to null, otherwise hibernate will do a flush before executing the find
                    // then trying to save a new company with existing public id will fail
                    customer.getProfile().setCompany(null);
                    companyRepository.findByPublicId(id)
                            .ifPresent(company -> {
                                customer.getProfile().setCompany(company);
                            });
                }
            }
            Customer saved = customerRepository.save(customer);
            return customerMapper.map(saved);
        } else {
            Customer entity = customerMapper.map(dto);
            return customerMapper.map(customerRepository.save(entity));
        }
    }

    public Optional<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email).map(customerMapper::map);
    }

    public CustomerDTO getCustomerByPublicId(String publicId) {
        return customerRepository.findByPublicId(publicId)
                .map(customerMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + publicId
                ));
    }
}
