package com.drivedash.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Base checked-to-runtime exception for the DriveDash platform.
 * Carries an {@link HttpStatus} so the global handler can map it directly.
 */
public class DrivedashException extends RuntimeException {

    private final HttpStatus status;

    public DrivedashException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public DrivedashException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ---------- convenience factory methods ----------

    public static DrivedashException notFound(String message) {
        return new DrivedashException(message, HttpStatus.NOT_FOUND);
    }

    public static DrivedashException badRequest(String message) {
        return new DrivedashException(message, HttpStatus.BAD_REQUEST);
    }

    public static DrivedashException forbidden(String message) {
        return new DrivedashException(message, HttpStatus.FORBIDDEN);
    }

    public static DrivedashException conflict(String message) {
        return new DrivedashException(message, HttpStatus.CONFLICT);
    }

    public static DrivedashException internalError(String message) {
        return new DrivedashException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
