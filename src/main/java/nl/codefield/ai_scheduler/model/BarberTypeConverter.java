package nl.codefield.ai_scheduler.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BarberTypeConverter implements AttributeConverter<BarberType, String> {

    @Override
    public String convertToDatabaseColumn(BarberType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public BarberType convertToEntityAttribute(String dbData) {
        return dbData != null ? BarberType.valueOf(dbData) : null;
    }
}
