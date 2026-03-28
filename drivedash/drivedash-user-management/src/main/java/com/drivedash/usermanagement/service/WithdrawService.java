package com.drivedash.usermanagement.service;

import com.drivedash.core.exception.DrivedashException;
import com.drivedash.usermanagement.dto.WithdrawMethodRequest;
import com.drivedash.usermanagement.dto.WithdrawRequestAction;
import com.drivedash.usermanagement.entity.WithdrawMethod;
import com.drivedash.usermanagement.entity.WithdrawRequest;
import com.drivedash.usermanagement.repository.WithdrawMethodRepository;
import com.drivedash.usermanagement.repository.WithdrawRequestRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawMethodRepository methodRepository;
    private final WithdrawRequestRepository requestRepository;
    private final ObjectMapper objectMapper;

    // ── Methods ───────────────────────────────────────────────────────────────

    public List<WithdrawMethod> getAllMethods() {
        return methodRepository.findAll(Sort.by(Sort.Direction.DESC, "isDefault").and(
                Sort.by(Sort.Direction.ASC, "methodName")));
    }

    public List<WithdrawMethod> getActiveMethods() {
        return methodRepository.findAllByActiveTrueOrderByIsDefaultDescMethodNameAsc();
    }

    public WithdrawMethod findMethodById(Long id) {
        return methodRepository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Withdraw method not found"));
    }

    @Transactional
    public WithdrawMethod createMethod(WithdrawMethodRequest req) {
        if (methodRepository.existsByMethodName(req.getMethodName())) {
            throw DrivedashException.conflict("Method name already exists");
        }
        List<Map<String, Object>> fields = parseFields(req.getMethodFieldsJson());
        if (req.isDefault()) clearDefault();
        return methodRepository.save(WithdrawMethod.builder()
                .methodName(req.getMethodName())
                .methodFields(fields)
                .isDefault(req.isDefault())
                .active(req.isActive())
                .build());
    }

    @Transactional
    public WithdrawMethod updateMethod(Long id, WithdrawMethodRequest req) {
        WithdrawMethod method = findMethodById(id);
        if (methodRepository.existsByMethodNameAndIdNot(req.getMethodName(), id)) {
            throw DrivedashException.conflict("Method name already exists");
        }
        if (req.isDefault() && !method.isDefault()) clearDefault();
        method.setMethodName(req.getMethodName());
        method.setMethodFields(parseFields(req.getMethodFieldsJson()));
        method.setDefault(req.isDefault());
        method.setActive(req.isActive());
        return methodRepository.save(method);
    }

    @Transactional
    public void deleteMethod(Long id) {
        methodRepository.delete(findMethodById(id));
    }

    @Transactional
    public void toggleMethodStatus(Long id, boolean active) {
        WithdrawMethod method = findMethodById(id);
        method.setActive(active);
        methodRepository.save(method);
    }

    // ── Requests ──────────────────────────────────────────────────────────────

    public Page<WithdrawRequest> getRequests(String status, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return switch (status) {
            case "approved" -> requestRepository.findAllByApproved(true, pr);
            case "rejected" -> requestRepository.findAllByApproved(false, pr);
            default -> requestRepository.findAllByApprovedIsNull(pr);
        };
    }

    public WithdrawRequest findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Withdraw request not found"));
    }

    @Transactional
    public void processRequest(Long id, WithdrawRequestAction action) {
        WithdrawRequest req = findRequestById(id);
        req.setApproved(action.isApproved());
        if (!action.isApproved() && StringUtils.hasText(action.getRejectionCause())) {
            req.setRejectionCause(action.getRejectionCause());
        }
        requestRepository.save(req);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void clearDefault() {
        methodRepository.findAll().forEach(m -> {
            if (m.isDefault()) {
                m.setDefault(false);
                methodRepository.save(m);
            }
        });
    }

    private List<Map<String, Object>> parseFields(String json) {
        if (!StringUtils.hasText(json)) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
