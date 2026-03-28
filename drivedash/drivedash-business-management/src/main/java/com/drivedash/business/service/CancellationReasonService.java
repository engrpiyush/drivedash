package com.drivedash.business.service;

import com.drivedash.business.dto.CancellationReasonDto;
import com.drivedash.business.dto.CancellationReasonRequest;
import com.drivedash.business.entity.CancellationReason;
import com.drivedash.business.entity.CancellationUserType;
import com.drivedash.business.repository.CancellationReasonRepository;
import com.drivedash.core.annotation.Auditable;
import com.drivedash.core.exception.DrivedashException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD service for {@link CancellationReason}.
 * Mirrors Laravel's {@code CancellationReasonService}.
 */
@Service
@RequiredArgsConstructor
public class CancellationReasonService {

    private final CancellationReasonRepository repository;

    @Auditable(entityClass = CancellationReason.class, action = "CREATE")
    @Transactional
    public CancellationReasonDto create(CancellationReasonRequest request) {
        CancellationReason reason = repository.save(CancellationReason.builder()
                .title(request.getTitle())
                .cancellationType(request.getCancellationType())
                .userType(request.getUserType())
                .active(request.isActive())
                .build());
        return toDto(reason);
    }

    @Auditable(entityClass = CancellationReason.class, action = "UPDATE")
    @Transactional
    public CancellationReasonDto update(UUID id, CancellationReasonRequest request) {
        CancellationReason reason = findOrThrow(id);
        reason.setTitle(request.getTitle());
        reason.setCancellationType(request.getCancellationType());
        reason.setUserType(request.getUserType());
        reason.setActive(request.isActive());
        return toDto(repository.save(reason));
    }

    @Auditable(entityClass = CancellationReason.class, action = "STATUS_CHANGE")
    @Transactional
    public void updateStatus(UUID id, boolean active) {
        CancellationReason reason = findOrThrow(id);
        reason.setActive(active);
        repository.save(reason);
    }

    @Auditable(entityClass = CancellationReason.class, action = "DELETE")
    @Transactional
    public void delete(UUID id) {
        CancellationReason reason = findOrThrow(id);
        repository.delete(reason);
    }

    @Transactional(readOnly = true)
    public CancellationReasonDto findById(UUID id) {
        return toDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CancellationReasonDto> findAllActive() {
        return repository.findAllByActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CancellationReasonDto> findAllByUserType(CancellationUserType userType) {
        return repository.findAllByUserType(userType).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CancellationReasonDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CancellationReason findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Cancellation reason not found"));
    }

    private CancellationReasonDto toDto(CancellationReason reason) {
        return CancellationReasonDto.builder()
                .id(reason.getId())
                .title(reason.getTitle())
                .cancellationType(reason.getCancellationType())
                .userType(reason.getUserType())
                .active(reason.isActive())
                .build();
    }
}
