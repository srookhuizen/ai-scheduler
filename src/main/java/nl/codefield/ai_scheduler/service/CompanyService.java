package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.AddressDTO;
import nl.codefield.ai_scheduler.dto.CompanyDTO;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.exception.ResourceNotFoundException;
import nl.codefield.ai_scheduler.mapper.CompanyMapper;
import nl.codefield.ai_scheduler.model.BarberType;
import nl.codefield.ai_scheduler.repository.CompanyRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CustomerService customerService;

    @Value("${ai.scheduler.default-radius:10}")
    private Integer defaultRadius;

    public Collection<CompanyDTO> getAllCompanies() {
        //return companyMapper.map(companyRepository.findAll());
        return CompanyTestData.createMockCompanies().stream().map(companyMapper::map).toList();
    }

    public List<CompanyDTO> getCompaniesByType(BarberType barberType) {
        if (barberType == null) {
            return List.of();
        }

        // return companyRepository.findByBarberType(barberType);
        return CompanyTestData.createMockCompanies().stream()
                .filter(company -> company.getBarberType() == barberType)
                .map(companyMapper::map)
                .toList();
    }

    public CompanyDTO getCompanyByPublicId(String publicId) {
        return companyRepository.findByPublicId(publicId)
                .map(companyMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with ID: " + publicId
                ));
    }

    public List<CompanyDTO> findCompaniesWithinRadius(String email) {
        Optional<CustomerDTO> optionalCustomer = customerService.findByEmail(email);
        if (optionalCustomer.isPresent()) {
            CustomerDTO customer = optionalCustomer.get();
            AddressDTO address = customer.getAddress();
            String barberType = customer.getProfile().getBarberType().getValue();
            List<CompanyDTO> companies = companyMapper.map(companyRepository
                    .findCompaniesWithinRadius(address.getLatitude(), address.getLongitude(),
                            defaultRadius, barberType));
            if (CollectionUtils.isEmpty(companies)) {
                log.warn("No companies found for latitude {} and longitude {} and barber type {}",
                        address.getLatitude(), address.getLongitude(), barberType);
            }
            return companies;
        }
        log.warn("No customer found with email {}", email);
        return List.of();
    }
}
