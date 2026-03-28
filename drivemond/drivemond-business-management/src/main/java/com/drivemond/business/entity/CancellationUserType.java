package com.drivemond.business.entity;

/**
 * Which user type a cancellation reason is shown to.
 * Maps to the {@code user_type} column in {@code cancellation_reasons}.
 */
public enum CancellationUserType {
    DRIVER,
    CUSTOMER;

    public String value() {
        return name().toLowerCase();
    }
}
