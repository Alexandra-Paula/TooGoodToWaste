package org.application.waste.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AvailabilityConverter implements AttributeConverter<Availability, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Availability availability) {
        return availability != null ? availability.getCode() : null;
    }

    @Override
    public Availability convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        for (Availability a : Availability.values()) {
            if (a.getCode() == code) {
                return a;
            }
        }
        throw new IllegalArgumentException("Cod invalid pentru Availability: " + code);
    }
}