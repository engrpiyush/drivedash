package com.drivemond.transaction.controller.api;

import com.drivemond.auth.entity.User;
import com.drivemond.transaction.entity.Transaction;
import com.drivemond.transaction.service.TransactionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionApiController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<Transaction>> list(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "all") String account,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = user.getId();
        return ResponseEntity.ok(transactionService.getByUser(userId, account, page, size));
    }
}
