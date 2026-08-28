package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO toDto(Customer customer);

    @Mapping(target = "appointments", ignore = true)
    Customer toEntity(CustomerDTO customerDto);

    @Mapping(target = "appointments", ignore = true)
    void map(CustomerDTO customerDto, @MappingTarget Customer customer);
}
