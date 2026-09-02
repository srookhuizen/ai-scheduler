package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.CompanyDTO;
import nl.codefield.ai_scheduler.model.Company;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true),
        uses = {BarberTypeMapper.class})
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Mapping(source = "publicId", target = "id")
    CompanyDTO map(Company company);

    @Mapping(source = "id", target = "publicId")
    @Mapping(target = "id", ignore = true)
    Company map(CompanyDTO dto);

    List<CompanyDTO> map(Collection<Company> companies);
}
