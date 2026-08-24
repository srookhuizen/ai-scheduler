package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO toDto(Customer customer);

    @Mapping(target = "appointments", ignore = true)
    Customer toEntity(CustomerDTO customerDto);
}
