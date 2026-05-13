package com.fathom.investment;

import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InvestmentHoldingService {
    private final InvestmentHoldingRepository repository;
    private final UserService userService;

    public InvestmentHoldingService(InvestmentHoldingRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public InvestmentDtos.InvestmentHoldingResponse create(UUID userId, InvestmentDtos.CreateInvestmentHoldingRequest request) {
        userService.getEntity(userId);
        InvestmentHolding holding = new InvestmentHolding();
        holding.setUserId(userId);
        holding.setAssetType(request.assetType());
        holding.setName(request.name());
        holding.setProvider(request.provider());
        holding.setSymbol(request.symbol());
        if (request.currency() != null) {
            holding.setCurrency(request.currency());
        }
        if (request.investedAmount() != null) {
            holding.setInvestedAmount(request.investedAmount());
        }
        if (request.currentValue() != null) {
            holding.setCurrentValue(request.currentValue());
        }
        holding.setAsOfDate(request.asOfDate());
        return toResponse(repository.save(holding));
    }

    public List<InvestmentDtos.InvestmentHoldingResponse> list(UUID userId) {
        userService.getEntity(userId);
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private InvestmentDtos.InvestmentHoldingResponse toResponse(InvestmentHolding h) {
        return new InvestmentDtos.InvestmentHoldingResponse(
                h.getId(), h.getUserId(), h.getAssetType(), h.getName(), h.getProvider(), h.getSymbol(), h.getCurrency(),
                h.getInvestedAmount(), h.getCurrentValue(), h.getAsOfDate(), h.isActive(), h.getCreatedAt(), h.getUpdatedAt());
    }
}
