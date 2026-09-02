package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.model.Customer;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = {AddressMapper.class, CompanyMapper.class, ProfileMapper.class})
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "publicId", target = "id")
    CustomerDTO map(Customer customer);

    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    Customer map(CustomerDTO customerDto);

    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    // Prevents MapStruct from overwriting existing fields with null if they are missing in the DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void map(CustomerDTO customerDto, @MappingTarget Customer customer);
}
