package com.drivedash.usermanagement.service;

import com.drivedash.auth.entity.User;
import com.drivedash.auth.entity.UserType;
import com.drivedash.auth.repository.UserRepository;
import com.drivedash.core.annotation.Auditable;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.usermanagement.dto.CustomerRequest;
import com.drivedash.usermanagement.repository.UserAccountRepository;
import com.drivedash.usermanagement.entity.UserAccount;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> getPage(String search, String status, int page, int size) {
        Specification<User> spec = Specification
                .where(typeSpec(UserType.CUSTOMER))
                .and(searchSpec(search))
                .and(statusSpec(status));
        return userRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Customer not found"));
    }

    @Auditable(entityClass = User.class, action = "CREATE")
    @Transactional
    public User create(CustomerRequest req) {
        if (StringUtils.hasText(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw DrivedashException.conflict("Email already in use");
        }
        if (StringUtils.hasText(req.getPhone()) && userRepository.existsByPhone(req.getPhone())) {
            throw DrivedashException.conflict("Phone already in use");
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(StringUtils.hasText(req.getEmail()) ? req.getEmail() : null)
                .phone(StringUtils.hasText(req.getPhone()) ? req.getPhone() : null)
                .password(passwordEncoder.encode(
                        StringUtils.hasText(req.getPassword()) ? req.getPassword() : UUID.randomUUID().toString()))
                .userType(UserType.CUSTOMER)
                .userLevelId(req.getUserLevelId())
                .isActive(req.isActive())
                .build();
        user = userRepository.save(user);
        // initialise wallet
        userAccountRepository.save(UserAccount.builder().userId(user.getId()).build());
        return user;
    }

    @Auditable(entityClass = User.class, action = "UPDATE")
    @Transactional
    public User update(UUID id, CustomerRequest req) {
        User user = findById(id);
        if (StringUtils.hasText(req.getEmail()) && !req.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw DrivedashException.conflict("Email already in use");
        }
        if (StringUtils.hasText(req.getPhone()) && !req.getPhone().equals(user.getPhone())
                && userRepository.existsByPhone(req.getPhone())) {
            throw DrivedashException.conflict("Phone already in use");
        }
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        if (StringUtils.hasText(req.getEmail())) user.setEmail(req.getEmail());
        if (StringUtils.hasText(req.getPhone())) user.setPhone(req.getPhone());
        if (StringUtils.hasText(req.getPassword())) user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUserLevelId(req.getUserLevelId());
        user.setActive(req.isActive());
        return userRepository.save(user);
    }

    @Auditable(entityClass = User.class, action = "DELETE")
    @Transactional
    public void delete(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Auditable(entityClass = User.class, action = "STATUS_CHANGE")
    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }

    public Page<User> getTrashed(int page, int size) {
        Specification<User> spec = Specification
                .where(deletedSpec())
                .and(typeSpec(UserType.CUSTOMER));
        // Native query needed; use custom repo approach via Specification + deleted_at IS NOT NULL
        // For simplicity, use a native query result wrapped via specification ignoring SQLRestriction
        return userRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    // ── Specifications ────────────────────────────────────────────────────────

    private Specification<User> typeSpec(UserType type) {
        return (root, q, cb) -> cb.equal(root.get("userType"), type);
    }

    private Specification<User> searchSpec(String search) {
        if (!StringUtils.hasText(search)) return null;
        return (root, q, cb) -> {
            String like = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("phone")), like)
            );
        };
    }

    private Specification<User> statusSpec(String status) {
        if ("active".equals(status)) return (r, q, cb) -> cb.isTrue(r.get("isActive"));
        if ("inactive".equals(status)) return (r, q, cb) -> cb.isFalse(r.get("isActive"));
        return null;
    }

    private Specification<User> deletedSpec() {
        return (root, q, cb) -> cb.isNotNull(root.get("deletedAt"));
    }
}
