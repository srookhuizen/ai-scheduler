package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.CompanyDTO;
import nl.codefield.ai_scheduler.model.Company;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyDTO toDto(Company company);

    Company toEntity(CompanyDTO dto);
}
