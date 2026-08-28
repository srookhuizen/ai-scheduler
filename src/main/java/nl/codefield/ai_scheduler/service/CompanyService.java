package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import nl.codefield.ai_scheduler.dto.CompanyDTO;
import nl.codefield.ai_scheduler.mapper.CompanyMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyMapper companyMapper;

    public Collection<CompanyDTO> getAllCompanies() {
        return CompanyTestData.createMockCompanies().stream().map(companyMapper::toDto).toList();
    }
}
