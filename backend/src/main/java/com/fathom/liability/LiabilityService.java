package com.fathom.liability;

import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LiabilityService {
    private final LiabilityRepository repository;
    private final UserService userService;

    public LiabilityService(LiabilityRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public LiabilityDtos.LiabilityResponse create(UUID userId, LiabilityDtos.CreateLiabilityRequest request) {
        userService.getEntity(userId);
        Liability liability = new Liability();
        liability.setUserId(userId);
        liability.setLiabilityType(request.liabilityType());
        liability.setName(request.name());
        liability.setLender(request.lender());
        if (request.currency() != null) {
            liability.setCurrency(request.currency());
        }
        liability.setPrincipalAmount(request.principalAmount());
        liability.setOutstandingAmount(request.outstandingAmount());
        liability.setInterestRate(request.interestRate());
        liability.setEmiAmount(request.emiAmount());
        liability.setStartDate(request.startDate());
        liability.setEndDate(request.endDate());
        return toResponse(repository.save(liability));
    }

    public List<LiabilityDtos.LiabilityResponse> list(UUID userId) {
        userService.getEntity(userId);
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private LiabilityDtos.LiabilityResponse toResponse(Liability l) {
        return new LiabilityDtos.LiabilityResponse(
                l.getId(), l.getUserId(), l.getLiabilityType(), l.getName(), l.getLender(), l.getCurrency(),
                l.getPrincipalAmount(), l.getOutstandingAmount(), l.getInterestRate(), l.getEmiAmount(),
                l.getStartDate(), l.getEndDate(), l.isActive(), l.getCreatedAt(), l.getUpdatedAt());
    }
}
