package nl.codefield.ai_scheduler.service.address;

import lombok.RequiredArgsConstructor;
import nl.codefield.ai_scheduler.dto.AddressDTO;
import nl.codefield.ai_scheduler.exception.AddressNotFoundException;
import nl.codefield.ai_scheduler.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressLookupService addressLookupService;
    private final AddressMapper addressMapper;

    public Optional<AddressDTO> getAddress(String postalCode, String houseNumber) {
        try {
            return Optional.ofNullable(addressLookupService.findAndBuildAddress(postalCode, houseNumber))
                    .map(addressMapper::map);
        } catch (AddressNotFoundException e) {
            return Optional.empty();
        }
    }
}
