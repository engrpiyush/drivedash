package com.drivemond.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Base checked-to-runtime exception for the DriveMond platform.
 * Carries an {@link HttpStatus} so the global handler can map it directly.
 */
public class DrivemondException extends RuntimeException {

    private final HttpStatus status;

    public DrivemondException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public DrivemondException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ---------- convenience factory methods ----------

    public static DrivemondException notFound(String message) {
        return new DrivemondException(message, HttpStatus.NOT_FOUND);
    }

    public static DrivemondException badRequest(String message) {
        return new DrivemondException(message, HttpStatus.BAD_REQUEST);
    }

    public static DrivemondException forbidden(String message) {
        return new DrivemondException(message, HttpStatus.FORBIDDEN);
    }

    public static DrivemondException conflict(String message) {
        return new DrivemondException(message, HttpStatus.CONFLICT);
    }

    public static DrivemondException internalError(String message) {
        return new DrivemondException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
