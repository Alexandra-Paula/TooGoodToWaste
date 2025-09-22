package org.application.waste.enums;

import lombok.Getter;

@Getter
public enum RecommendationStatus {
    PREFERAT(20),
    IGNORAT(10);

    private final int code;

    RecommendationStatus(int code) {
        this.code = code;
    }
}