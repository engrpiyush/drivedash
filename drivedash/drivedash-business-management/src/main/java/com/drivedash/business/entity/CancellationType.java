package com.drivedash.business.entity;

/**
 * Defines at which stage of a trip a cancellation reason applies.
 * Maps to the {@code cancellation_type} column in {@code cancellation_reasons}.
 */
public enum CancellationType {
    ONGOING_RIDE,
    ACCEPTED_RIDE;

    public String value() {
        return name().toLowerCase();
    }
}
