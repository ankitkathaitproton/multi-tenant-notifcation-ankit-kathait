package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.UpdateRateLimitRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.TenantRateLimit;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.TenantRateLimitRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/tenants/{tenantId}/rate-limits")
public class RateLimitResource {

    private final TenantRateLimitRepository tenantRateLimitRepository;

    public RateLimitResource(TenantRateLimitRepository tenantRateLimitRepository) {
        this.tenantRateLimitRepository = tenantRateLimitRepository;
    }

    @PutMapping
    public ResponseEntity<TenantRateLimit> updateRateLimits(@RequestHeader("X-User-Role") String userRole,
                                                            @PathVariable UUID tenantId,
                                                            @Valid @RequestBody UpdateRateLimitRequest request) {
        if (!"PLATFORM_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TenantRateLimit rateLimit = tenantRateLimitRepository.findByTenantId(tenantId)
                .orElse(new TenantRateLimit(null, tenantId, 0, 0, null));

        rateLimit.setRequestsPerSecond(request.getRequestsPerSecond());
        rateLimit.setMaxConcurrentJobs(request.getMaxConcurrentJobs());

        TenantRateLimit updatedRateLimit = tenantRateLimitRepository.save(rateLimit);
        return ResponseEntity.ok(updatedRateLimit);
    }
}