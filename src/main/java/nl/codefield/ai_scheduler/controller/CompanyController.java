package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.CompanyDTO;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.entity.BarberType;
import nl.codefield.ai_scheduler.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController()
@RequestMapping("/company")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/companies")
    public ResponseEntity<Collection<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CompanyDTO>> getCompaniesByType(@PathVariable("type") String type) {
        try {
            BarberType barberType = BarberType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(companyService.getCompaniesByType(barberType));
        } catch (IllegalArgumentException e) {
            // if company type is unknown.
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyByPublicId(@PathVariable String id) {
        CompanyDTO companyDto = companyService.getCompanyByPublicId(id);
        return ResponseEntity.ok(companyDto);
    }

    @PostMapping("/find")
    public ResponseEntity<List<CompanyDTO>> findCompanies(@RequestBody CustomerDTO dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(companyService.findCompaniesWithinRadius(dto.getEmail()));
    }
}
