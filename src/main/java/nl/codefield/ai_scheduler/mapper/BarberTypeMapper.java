package nl.codefield.ai_scheduler.mapper;

import nl.codefield.ai_scheduler.dto.BarberTypeDTO;
import nl.codefield.ai_scheduler.entity.BarberType;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BarberTypeMapper {

    default BarberTypeDTO map(BarberType barberType) {
        if (barberType == null) {
            return null;
        }
        return BarberTypeDTO.builder()
                .value(barberType.name())
                .name(barberType.getDisplayName())
                .build();
    }

    default BarberType map(BarberTypeDTO dto) {
        if (dto == null || dto.getValue() == null) {
            return null;
        }
        return BarberType.valueOf(dto.getValue());
    }
}
