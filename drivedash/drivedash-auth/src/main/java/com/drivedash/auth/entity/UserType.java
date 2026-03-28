package com.drivedash.auth.entity;

/**
 * Enumeration of all user types in the platform.
 * Replaces Laravel's string constants scattered across models.
 */
public enum UserType {
    SUPER_ADMIN,
    ADMIN,
    EMPLOYEE,
    DRIVER,
    CUSTOMER
}
