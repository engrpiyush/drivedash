package com.drivemond.vehicle.enums;

public enum FuelType {
    PETROL, DIESEL, CNG, LPG, ELECTRIC, HYBRID;

    public String label() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
