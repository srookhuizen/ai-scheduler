package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.AddressDTO;
import nl.codefield.ai_scheduler.service.address.PdokSearchResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AddressMapper {

    AddressDTO toDTO(PdokSearchResponse.PdokAddressDoc address);
}
