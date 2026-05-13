package com.fathom.account;

import com.fathom.common.ResourceNotFoundException;
import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FinancialAccountService {
    private final FinancialAccountRepository repository;
    private final UserService userService;

    public FinancialAccountService(FinancialAccountRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public AccountDtos.FinancialAccountResponse create(UUID userId, AccountDtos.CreateFinancialAccountRequest request) {
        userService.getEntity(userId);
        FinancialAccount account = new FinancialAccount();
        account.setUserId(userId);
        account.setName(request.name());
        account.setInstitutionName(request.institutionName());
        account.setAccountType(request.accountType());
        if (request.currency() != null) account.setCurrency(request.currency());
        account.setMaskedIdentifier(request.maskedIdentifier());
        return toResponse(repository.save(account));
    }

    public List<AccountDtos.FinancialAccountResponse> listByUser(UUID userId) {
        userService.getEntity(userId);
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public AccountDtos.FinancialAccountResponse get(UUID accountId) {
        return toResponse(getEntity(accountId));
    }

    public FinancialAccount getEntity(UUID accountId) {
        return repository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private AccountDtos.FinancialAccountResponse toResponse(FinancialAccount a) { return new AccountDtos.FinancialAccountResponse(a.getId(),a.getUserId(),a.getName(),a.getInstitutionName(),a.getAccountType(),a.getCurrency(),a.getMaskedIdentifier(),a.isActive(),a.getCreatedAt(),a.getUpdatedAt()); }
}
