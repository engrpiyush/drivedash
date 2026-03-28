package com.drivedash.business.entity;

/**
 * Enumerates every {@code settings_type} value used in the {@code business_settings} table.
 * Replaces the scattered PHP constants in {@code BusinessSettingType.php}.
 */
public enum SettingsType {

    BUSINESS_INFORMATION,
    BUSINESS_SETTINGS,
    DRIVER_SETTINGS,
    CUSTOMER_SETTINGS,
    NOTIFICATION_SETTINGS,
    PAGES_SETTINGS,
    LANDING_PAGES_SETTINGS,
    SERVER_KEY,
    EMAIL_CONFIG,
    SMS_CONFIG,
    PAYMENT_CONFIG,
    SOCIAL_LOGIN,
    TRIP_SETTINGS,
    TRIP_FARE_SETTINGS,
    RECAPTCHA,
    GOOGLE_MAP_API,
    SYSTEM_LANGUAGE,
    LANGUAGE_SETTINGS;

    /** Returns the database string value (lowercase with underscores). */
    public String value() {
        return name().toLowerCase();
    }
}
