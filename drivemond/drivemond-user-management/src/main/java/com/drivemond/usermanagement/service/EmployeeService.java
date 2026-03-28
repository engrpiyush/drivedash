package com.drivemond.usermanagement.service;

import com.drivemond.auth.entity.Role;
import com.drivemond.auth.entity.User;
import com.drivemond.auth.entity.UserType;
import com.drivemond.auth.repository.RoleRepository;
import com.drivemond.auth.repository.UserRepository;
import com.drivemond.core.exception.DrivemondException;
import com.drivemond.usermanagement.dto.EmployeeRequest;
import com.drivemond.usermanagement.dto.RoleRequest;
import jakarta.transaction.Transactional;
import java.util.List;
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
public class EmployeeService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Employee CRUD ─────────────────────────────────────────────────────────

    public Page<User> getPage(String search, String status, int page, int size) {
        Specification<User> spec = Specification
                .where(typeSpec(UserType.EMPLOYEE))
                .and(searchSpec(search))
                .and(statusSpec(status));
        return userRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Employee not found"));
    }

    @Transactional
    public User create(EmployeeRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw DrivemondException.conflict("Email already in use");
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(StringUtils.hasText(req.getPhone()) ? req.getPhone() : null)
                .password(passwordEncoder.encode(
                        StringUtils.hasText(req.getPassword()) ? req.getPassword() : UUID.randomUUID().toString()))
                .userType(UserType.EMPLOYEE)
                .isActive(req.isActive())
                .build();
        if (req.getRoleId() != null) {
            roleRepository.findById(req.getRoleId()).ifPresent(role -> user.getRoles().add(role));
        }
        return userRepository.save(user);
    }

    @Transactional
    public User update(UUID id, EmployeeRequest req) {
        User user = findById(id);
        if (!req.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw DrivemondException.conflict("Email already in use");
        }
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        if (StringUtils.hasText(req.getPhone())) user.setPhone(req.getPhone());
        if (StringUtils.hasText(req.getPassword())) user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setActive(req.isActive());
        user.getRoles().clear();
        if (req.getRoleId() != null) {
            roleRepository.findById(req.getRoleId()).ifPresent(role -> user.getRoles().add(role));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.delete(findById(id));
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }

    // ── Role CRUD ─────────────────────────────────────────────────────────────

    public Page<Role> getRolePage(int page, int size) {
        return roleRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name")));
    }

    public List<Role> getActiveRoles() {
        return roleRepository.findAllByActiveTrue();
    }

    public Role findRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Role not found"));
    }

    @Transactional
    public Role createRole(RoleRequest req) {
        if (roleRepository.existsByName(req.getName())) {
            throw DrivemondException.conflict("Role name already exists");
        }
        return roleRepository.save(Role.builder()
                .name(req.getName())
                .modules(req.getModules())
                .active(req.isActive())
                .build());
    }

    @Transactional
    public Role updateRole(UUID id, RoleRequest req) {
        Role role = findRoleById(id);
        if (roleRepository.existsByNameAndIdNot(req.getName(), id)) {
            throw DrivemondException.conflict("Role name already exists");
        }
        role.setName(req.getName());
        role.setModules(req.getModules());
        role.setActive(req.isActive());
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(UUID id) {
        roleRepository.delete(findRoleById(id));
    }

    @Transactional
    public void toggleRoleStatus(UUID id, boolean active) {
        Role role = findRoleById(id);
        role.setActive(active);
        roleRepository.save(role);
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
                    cb.like(cb.lower(root.get("email")), like)
            );
        };
    }

    private Specification<User> statusSpec(String status) {
        if ("active".equals(status)) return (r, q, cb) -> cb.isTrue(r.get("isActive"));
        if ("inactive".equals(status)) return (r, q, cb) -> cb.isFalse(r.get("isActive"));
        return null;
    }
}
