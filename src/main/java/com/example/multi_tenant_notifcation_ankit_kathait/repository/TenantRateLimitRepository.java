package com.example.multi_tenant_notifcation_ankit_kathait.repository;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.TenantRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRateLimitRepository extends JpaRepository<TenantRateLimit, UUID> {

    Optional<TenantRateLimit> findByTenantId(UUID tenantId);
}

