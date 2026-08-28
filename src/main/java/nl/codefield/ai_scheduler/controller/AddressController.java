package nl.codefield.ai_scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.dto.AddressDTO;
import nl.codefield.ai_scheduler.service.address.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/address")
@RequiredArgsConstructor
@Slf4j
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/find")
    public ResponseEntity<AddressDTO> getAddress(@RequestBody FindAddressRequest findAddressRequest) {
        return addressService.getAddress(findAddressRequest.getPostalCode(), findAddressRequest.getHouseNumber())
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
