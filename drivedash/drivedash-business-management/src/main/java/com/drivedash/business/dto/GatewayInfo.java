package com.drivedash.business.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * Read-only view of one payment gateway: combines the static catalogue entry
 * (slug, title, required credential fields) with the admin-configured values
 * stored in {@code business_settings}.
 */
@Getter
@Builder
public class GatewayInfo {

    private final String slug;
    private final String title;
    /** Bootstrap icon class, e.g. {@code bi-credit-card}. */
    private final String iconClass;
    /** Credential field names required by this gateway (same for live and test). */
    private final List<String> credentialFields;

    /** 1 = active / enabled, 0 = inactive. */
    private final int status;
    /** {@code "live"} or {@code "test"}. */
    private final String mode;

    /** Stored live credentials (keyed by field name). */
    private final Map<String, Object> liveValues;
    /** Stored test credentials (keyed by field name). */
    private final Map<String, Object> testValues;

    public boolean isActive() {
        return status == 1;
    }

    public boolean isLiveMode() {
        return "live".equals(mode);
    }

    /** Human-readable label for a credential field, e.g. {@code secret_key} → "Secret Key". */
    public static String fieldLabel(String fieldName) {
        String[] parts = fieldName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(' ');
            }
        }
        return sb.toString().trim();
    }
}
