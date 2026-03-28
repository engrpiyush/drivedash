package com.drivedash.auth.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Core user entity – maps to the {@code users} table.
 *
 * <p>Implements {@link UserDetails} so it can be used directly by Spring Security,
 * avoiding a separate wrapper class. Roles are eager-loaded because they are
 * required on every authenticated request.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", unique = true, length = 191)
    private String email;

    @Column(name = "phone", unique = true, length = 30)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private UserType userType;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Column(name = "referral_code", length = 20)
    private String referralCode;

    @Column(name = "remember_token", length = 100)
    private String rememberToken;

    @Column(name = "user_level_id", columnDefinition = "CHAR(36)")
    private UUID userLevelId;

    @Column(name = "full_name", length = 191)
    private String fullName;

    @Column(name = "identification_number", length = 191)
    private String identificationNumber;

    @Column(name = "identification_type", length = 25)
    private String identificationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "identification_image", columnDefinition = "JSON")
    private List<String> identificationImage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "other_documents", columnDefinition = "JSON")
    private List<String> otherDocuments;

    @Column(name = "fcm_token", length = 191)
    private String fcmToken;

    @Builder.Default
    @Column(name = "loyalty_points", nullable = false)
    private double loyaltyPoints = 0.0;

    @Builder.Default
    @Column(name = "failed_attempt", nullable = false)
    private int failedAttempt = 0;

    @Builder.Default
    @Column(name = "is_temp_blocked", nullable = false)
    private boolean isTempBlocked = false;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // ── UserDetails contract ────────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    /** Spring Security uses email as the username principal. */
    @Override
    public String getUsername() {
        return email != null ? email : phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive && !isDeleted();
    }

    /** Convenience – full display name. */
    public String getFullName() {
        return lastName != null ? firstName + " " + lastName : firstName;
    }
}
