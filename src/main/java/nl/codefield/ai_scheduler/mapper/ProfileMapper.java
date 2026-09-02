package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.ProfileDTO;
import nl.codefield.ai_scheduler.model.Profile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true),
        uses = {CompanyMapper.class, BarberTypeMapper.class})
public interface ProfileMapper {

    Profile map(ProfileDTO dto);

    ProfileDTO map(Profile profile);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void map(ProfileDTO dto, @MappingTarget Profile profile);
}

