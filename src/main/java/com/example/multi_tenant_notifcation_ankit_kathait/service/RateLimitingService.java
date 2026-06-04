package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.TenantRateLimit;
import com.example.multi_tenant_notifcation_ankit_kathait.exception.RateLimitExceededException;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.TenantRateLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RateLimitingService {

    private final TenantRateLimitRepository tenantRateLimitRepository;

    public RateLimitingService(TenantRateLimitRepository tenantRateLimitRepository) {
        this.tenantRateLimitRepository = tenantRateLimitRepository;
    }

    @Transactional
    public void checkRateLimit(UUID tenantId) {
        TenantRateLimit rateLimit = tenantRateLimitRepository.findByTenantId(tenantId)
                .orElse(new TenantRateLimit(null, tenantId, 10, 5, null)); // Default limits

        Instant now = Instant.now();
        if (rateLimit.getLastRequestAt() != null &&
                rateLimit.getLastRequestAt().isAfter(now.minusSeconds(1))) {
            throw new RateLimitExceededException("Rate limit exceeded for tenant: " + tenantId);
        }

        rateLimit.setLastRequestAt(now);
        tenantRateLimitRepository.save(rateLimit);
    }
}