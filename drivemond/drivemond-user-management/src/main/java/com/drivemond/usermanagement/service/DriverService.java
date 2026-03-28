package com.drivemond.usermanagement.service;

import com.drivemond.auth.entity.User;
import com.drivemond.auth.entity.UserType;
import com.drivemond.auth.repository.UserRepository;
import com.drivemond.core.exception.DrivemondException;
import com.drivemond.core.util.FileStorageService;
import com.drivemond.usermanagement.dto.DriverRequest;
import com.drivemond.usermanagement.entity.DriverDetail;
import com.drivemond.usermanagement.entity.UserAccount;
import com.drivemond.usermanagement.repository.DriverDetailRepository;
import com.drivemond.usermanagement.repository.UserAccountRepository;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final UserRepository userRepository;
    private final DriverDetailRepository driverDetailRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorage;

    public Page<User> getPage(String search, String status, int page, int size) {
        Specification<User> spec = Specification
                .where(typeSpec(UserType.DRIVER))
                .and(searchSpec(search))
                .and(statusSpec(status));
        return userRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Driver not found"));
    }

    @Transactional
    public User create(DriverRequest req) {
        if (userRepository.existsByPhone(req.getPhone())) {
            throw DrivemondException.conflict("Phone already in use");
        }
        if (StringUtils.hasText(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw DrivemondException.conflict("Email already in use");
        }
        String profileImage = null;
        if (req.getProfileImageFile() != null && !req.getProfileImageFile().isEmpty()) {
            profileImage = fileStorage.store(req.getProfileImageFile(), "profile");
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(StringUtils.hasText(req.getEmail()) ? req.getEmail() : null)
                .phone(req.getPhone())
                .password(passwordEncoder.encode(
                        StringUtils.hasText(req.getPassword()) ? req.getPassword() : UUID.randomUUID().toString()))
                .profileImage(profileImage)
                .userType(UserType.DRIVER)
                .userLevelId(req.getUserLevelId())
                .identificationNumber(req.getIdentificationNumber())
                .identificationType(req.getIdentificationType())
                .isActive(req.isActive())
                .build();
        user = userRepository.save(user);
        driverDetailRepository.save(DriverDetail.builder().userId(user.getId()).build());
        userAccountRepository.save(UserAccount.builder().userId(user.getId()).build());
        return user;
    }

    @Transactional
    public User update(UUID id, DriverRequest req) {
        User user = findById(id);
        if (!req.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(req.getPhone())) {
            throw DrivemondException.conflict("Phone already in use");
        }
        if (StringUtils.hasText(req.getEmail()) && !req.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw DrivemondException.conflict("Email already in use");
        }
        MultipartFile file = req.getProfileImageFile();
        if (file != null && !file.isEmpty()) {
            user.setProfileImage(fileStorage.store(file, "profile"));
        }
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        if (StringUtils.hasText(req.getEmail())) user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        if (StringUtils.hasText(req.getPassword())) user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUserLevelId(req.getUserLevelId());
        user.setIdentificationNumber(req.getIdentificationNumber());
        user.setIdentificationType(req.getIdentificationType());
        user.setActive(req.isActive());
        return userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }

    @Transactional
    public void approve(UUID id) {
        User user = findById(id);
        user.setActive(true);
        userRepository.save(user);
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
                    cb.like(cb.lower(root.get("phone")), like),
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
