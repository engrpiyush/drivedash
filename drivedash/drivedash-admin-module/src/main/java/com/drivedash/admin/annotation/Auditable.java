package com.drivedash.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method whose results should be captured in the
 * {@code activity_logs} table by
 * {@link com.drivedash.admin.aspect.ActivityLoggingAspect}.
 *
 * <p>Usage:
 * <pre>{@code
 * @Auditable(entityClass = Driver.class, action = "UPDATE")
 * public DriverDto updateDriver(UUID id, DriverRequest request) { ... }
 * }</pre>
 *
 * <p>The aspect reads the entity state <em>before</em> the method executes
 * (using the repository provided by the annotated class's module) and
 * captures the return value as the <em>after</em> snapshot.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /** The JPA entity class being mutated – used to set {@code logable_type}. */
    Class<?> entityClass();

    /** Human-readable action label, e.g. {@code "CREATE"}, {@code "UPDATE"}, {@code "DELETE"}. */
    String action() default "UPDATE";
}