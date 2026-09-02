package nl.codefield.ai_scheduler.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RegistrationTypeConverter implements AttributeConverter<RegistrationType, String> {

    @Override
    public String convertToDatabaseColumn(RegistrationType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public RegistrationType convertToEntityAttribute(String dbData) {
        return dbData != null ? RegistrationType.valueOf(dbData) : null;
    }
}
