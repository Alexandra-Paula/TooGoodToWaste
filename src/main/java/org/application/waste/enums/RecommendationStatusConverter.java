package org.application.waste.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RecommendationStatusConverter implements AttributeConverter<RecommendationStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RecommendationStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public RecommendationStatus convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        for (RecommendationStatus s : RecommendationStatus.values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Cod invalid pentru RecommendationStatus: " + code);
    }
}