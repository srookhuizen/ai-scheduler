package nl.codefield.ai_scheduler.model;

import lombok.Getter;

@Getter
public enum BarberType {
    SHOP("In-Shop Barber"),
    HOUSE_CALL("House Call Barber");

    private final String displayName;

    BarberType(String displayName) {
        this.displayName = displayName;
    }
}
