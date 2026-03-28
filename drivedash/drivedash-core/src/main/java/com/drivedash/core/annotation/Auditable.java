package com.drivedash.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method whose results should be captured in the
 * {@code activity_logs} table by the ActivityLoggingAspect in
 * {@code drivedash-admin-module}.
 *
 * <p>This annotation lives in {@code drivedash-core} so that every module
 * can use it without creating a circular dependency on the admin module.
 *
 * <p>Usage:
 * <pre>{@code
 * @Auditable(entityClass = Driver.class, action = "UPDATE")
 * public User updateDriver(UUID id, DriverRequest request) { ... }
 * }</pre>
 *
 * <p>The aspect captures the return value as the <em>after</em> snapshot.
 * For CREATE actions the entity UUID is extracted from the return value's
 * {@code getId()} method. For UPDATE / DELETE the first {@link java.util.UUID}
 * argument is used as the {@code logable_id}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /** The JPA entity class being mutated – used to set {@code logable_type}. */
    Class<?> entityClass();

    /** Human-readable action label, e.g. {@code "CREATE"}, {@code "UPDATE"}, {@code "DELETE"}. */
    String action() default "UPDATE";
}
