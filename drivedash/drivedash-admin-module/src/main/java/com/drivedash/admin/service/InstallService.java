package com.drivedash.admin.service;

import com.drivedash.auth.entity.Role;
import com.drivedash.auth.entity.User;
import com.drivedash.auth.entity.UserType;
import com.drivedash.auth.repository.RoleRepository;
import com.drivedash.auth.repository.UserRepository;
import com.drivedash.usermanagement.entity.UserAccount;
import com.drivedash.usermanagement.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InstallService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    /** Returns true if a super-admin user already exists. */
    public boolean isInstalled() {
        return userRepository.countByUserType(UserType.SUPER_ADMIN) > 0;
    }

    /** Tests whether the configured DataSource can actually connect. */
    public boolean testDatabaseConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(3);
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns current Java version string. */
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    /** Returns available heap memory in MB. */
    public long getAvailableMemoryMb() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    /** Creates the first super-admin user and their UserAccount. */
    @Transactional
    public void createSuperAdmin(String firstName, String lastName,
                                  String email, String phone, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("An account with this phone number already exists.");
        }
        // ensure ROLE_SUPER_ADMIN role exists
        Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name("ROLE_SUPER_ADMIN").active(true).build()));

        Set<Role> roles = new HashSet<>();
        roles.add(superAdminRole);

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(rawPassword))
                .userType(UserType.SUPER_ADMIN)
                .isActive(true)
                .roles(roles)
                .build();
        user = userRepository.save(user);

        UserAccount account = UserAccount.builder()
                .userId(user.getId())
                .build();
        userAccountRepository.save(account);
    }
}
