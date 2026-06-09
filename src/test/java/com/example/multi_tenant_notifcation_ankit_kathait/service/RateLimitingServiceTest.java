package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.TenantRateLimit;
import com.example.multi_tenant_notifcation_ankit_kathait.exception.RateLimitExceededException;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.TenantRateLimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RateLimitingServiceTest {

    @Mock
    private TenantRateLimitRepository tenantRateLimitRepository;

    @InjectMocks
    private RateLimitingService rateLimitingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckRateLimit() {
        TenantRateLimit rateLimit = new TenantRateLimit();
        rateLimit.setRequestsPerSecond(100);

        when(tenantRateLimitRepository.findByTenantId(any())).thenReturn(Optional.of(rateLimit));

        assertDoesNotThrow(() -> rateLimitingService.checkRateLimit(UUID.randomUUID()));
    }

    @Test
    void testCheckRateLimitExceeded() {
        TenantRateLimit rateLimit = new TenantRateLimit();
        rateLimit.setRequestsPerSecond(0);
        rateLimit.setLastRequestAt(Instant.now());

        when(tenantRateLimitRepository.findByTenantId(any())).thenReturn(Optional.of(rateLimit));

        assertThrows(RateLimitExceededException.class, () -> rateLimitingService.checkRateLimit(UUID.randomUUID()));
    }
}