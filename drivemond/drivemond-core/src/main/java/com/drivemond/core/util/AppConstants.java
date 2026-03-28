package com.drivemond.core.util;

/**
 * Application-wide constants – replaces Laravel's {@code Constant.php}.
 * All fields are public static final; instantiation is prevented.
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ---- User roles ----
    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_ADMIN       = "ROLE_ADMIN";
    public static final String ROLE_EMPLOYEE    = "ROLE_EMPLOYEE";
    public static final String ROLE_DRIVER      = "ROLE_DRIVER";
    public static final String ROLE_CUSTOMER    = "ROLE_CUSTOMER";

    // ---- Driver status ----
    public static final String DRIVER_STATUS_ACTIVE   = "active";
    public static final String DRIVER_STATUS_INACTIVE = "inactive";
    public static final String DRIVER_STATUS_PENDING  = "pending";

    // ---- Trip status ----
    public static final String TRIP_STATUS_PENDING   = "pending";
    public static final String TRIP_STATUS_ACCEPTED  = "accepted";
    public static final String TRIP_STATUS_ONGOING   = "ongoing";
    public static final String TRIP_STATUS_COMPLETED = "completed";
    public static final String TRIP_STATUS_CANCELLED = "cancelled";

    // ---- Transaction types ----
    public static final String TRANSACTION_CREDIT = "credit";
    public static final String TRANSACTION_DEBIT  = "debit";

    // ---- Pagination defaults ----
    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final int MAX_PAGE_SIZE     = 100;

    // ---- Date/time formats ----
    public static final String DATE_FORMAT     = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // ---- Storage paths ----
    public static final String UPLOAD_DIR            = "uploads/";
    public static final String PROFILE_PHOTO_DIR     = UPLOAD_DIR + "profile/";
    public static final String VEHICLE_PHOTO_DIR     = UPLOAD_DIR + "vehicle/";
    public static final String DOCUMENT_DIR          = UPLOAD_DIR + "document/";
    public static final String BANNER_DIR            = UPLOAD_DIR + "banner/";
    public static final String CHAT_ATTACHMENT_DIR   = UPLOAD_DIR + "chat/";
}
