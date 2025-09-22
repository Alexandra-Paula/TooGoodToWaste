package org.application.waste.enums;

import lombok.Getter;

@Getter
public enum Availability {
    DISPONIBIL(20),
    INDISPONIBIL(10);

    private final int code;

    Availability(int code) {
        this.code = code;
    }
}