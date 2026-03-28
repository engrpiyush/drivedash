package com.drivedash.business.entity;

import com.drivedash.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Key-value settings store – maps to the {@code business_settings} table.
 *
 * <p>The {@code value} field is stored as a MySQL JSON column and mapped to
 * {@code Map<String, Object>} via Hibernate's native JSON type support,
 * replacing Laravel's JSON-casted Eloquent attribute.
 *
 * <p>Each setting is identified by ({@code keyName}, {@code settingsType}).
 * The service layer always upserts using this composite lookup,
 * mirroring Laravel's {@code findOneBy(['key_name' => ..., 'settings_type' => ...])}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "business_settings",
    indexes = {
        @Index(name = "idx_bs_key_type", columnList = "key_name, settings_type")
    }
)
public class BusinessSetting extends BaseAuditEntity {

    @Column(name = "key_name", nullable = false, length = 191)
    private String keyName;

    /**
     * Flexible JSON payload – can be a scalar wrapped in a map ({@code {"v":"value"}})
     * or a structured object ({@code {"host":"smtp.example.com","port":587,...}}).
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value", nullable = false, columnDefinition = "JSON")
    private Map<String, Object> value;

    @Enumerated(EnumType.STRING)
    @Column(name = "settings_type", nullable = false, columnDefinition = "VARCHAR(191)")
    private SettingsType settingsType;
}
