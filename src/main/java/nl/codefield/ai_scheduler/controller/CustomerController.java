package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.model.Customer;
import nl.codefield.ai_scheduler.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/save")
    public ResponseEntity<Customer> save(@RequestBody CustomerDTO dto) {
        log.info("Saving customer {}", dto);
        Customer savedCustomer = customerService.save(dto);
        log.info("Saved customer {}", savedCustomer);
        return ResponseEntity.ok(savedCustomer);
    }
}
