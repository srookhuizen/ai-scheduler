package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.model.Customer;
import nl.codefield.ai_scheduler.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController()
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/find")
    public ResponseEntity<CustomerDTO> find(@RequestBody CustomerDTO dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        Optional<CustomerDTO> optionalCustomer = customerService.findByEmail(dto.getEmail());
        return optionalCustomer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<CustomerDTO> save(@RequestBody CustomerDTO dto) {
        log.info("Saving customer {}", dto);
        CustomerDTO savedCustomer = customerService.save(dto);
        log.info("Saved customer {}", savedCustomer);
        return ResponseEntity.ok(savedCustomer);
    }

    @PostMapping("/update")
    public ResponseEntity<CustomerDTO> update(@RequestBody CustomerDTO dto) {
        log.info("Updating customer {}", dto);
        return ResponseEntity.ok(customerService.save(dto));
    }
}
