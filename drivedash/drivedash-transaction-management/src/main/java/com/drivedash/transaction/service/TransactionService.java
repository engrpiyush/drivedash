package com.drivedash.transaction.service;

import com.drivedash.auth.entity.User;
import com.drivedash.auth.entity.UserType;
import com.drivedash.auth.repository.UserRepository;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.transaction.dto.WalletFundRequest;
import com.drivedash.transaction.entity.Transaction;
import com.drivedash.transaction.repository.TransactionRepository;
import com.drivedash.usermanagement.entity.UserAccount;
import com.drivedash.usermanagement.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final UserAccountRepository userAccountRepo;
    private final UserRepository userRepo;

    public Page<Transaction> getPage(String account, String attribute, String search,
                                     int page, int size) {
        Specification<Transaction> spec = Specification.where(null);

        if (StringUtils.hasText(account) && !"all".equals(account)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("account"), account));
        }
        if (StringUtils.hasText(attribute) && !"all".equals(attribute)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("attribute"), attribute));
        }
        if (StringUtils.hasText(search)) {
            final String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("attribute")), like),
                    cb.like(cb.lower(root.get("account")), like)
            ));
        }

        return transactionRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Page<Transaction> getWalletTransactions(String search, int page, int size) {
        Specification<Transaction> spec =
                (root, q, cb) -> cb.equal(root.get("account"), "wallet_balance");

        if (StringUtils.hasText(search)) {
            final String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("attribute")), like));
        }

        return transactionRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Page<Transaction> getByUser(UUID userId, String account, int page, int size) {
        Specification<Transaction> spec =
                (root, q, cb) -> cb.equal(root.get("userId"), userId);

        if (StringUtils.hasText(account) && !"all".equals(account)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("account"), account));
        }

        return transactionRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public void addWalletFund(WalletFundRequest req) {
        User customer = userRepo.findById(req.getCustomerId())
                .filter(u -> u.getUserType() == UserType.CUSTOMER)
                .orElseThrow(() -> DrivedashException.notFound("Customer not found"));

        UserAccount account = userAccountRepo.findByUserId(customer.getId())
                .orElseGet(() -> userAccountRepo.save(
                        UserAccount.builder().userId(customer.getId()).build()));

        BigDecimal newBalance = account.getWalletBalance().add(req.getAmount());
        account.setWalletBalance(newBalance);
        userAccountRepo.save(account);

        transactionRepo.save(Transaction.builder()
                .userId(customer.getId())
                .attributeId(customer.getId())
                .attribute("fund_by_admin")
                .account("wallet_balance")
                .credit(req.getAmount())
                .debit(BigDecimal.ZERO)
                .balance(newBalance)
                .build());
    }

    public List<User> getActiveCustomers() {
        return userRepo.findAll((root, q, cb) ->
                cb.and(
                        cb.equal(root.get("userType"), UserType.CUSTOMER),
                        cb.isTrue(root.get("isActive"))
                ));
    }
}
