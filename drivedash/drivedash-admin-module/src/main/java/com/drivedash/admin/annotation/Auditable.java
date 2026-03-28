package com.drivedash.admin.annotation;

/**
 * @deprecated Use {@link com.drivedash.core.annotation.Auditable} instead.
 *             This type alias is kept for backward compatibility only and will
 *             be removed in a future release.
 */
@Deprecated(since = "1.3.0", forRemoval = true)
public @interface Auditable {
    // Intentionally empty — the AOP pointcut now matches
    // com.drivedash.core.annotation.Auditable only.
}
