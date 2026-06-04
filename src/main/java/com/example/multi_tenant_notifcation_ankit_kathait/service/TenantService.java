package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.Tenant;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.TenantStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public Tenant createTenant(String name) {
        if (tenantRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tenant with name '" + name + "' already exists.");
        }
        Tenant newTenant = new Tenant();
        newTenant.setName(name);
        newTenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(newTenant);
    }

    @Transactional(readOnly = true)
    public Optional<Tenant> getTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Transactional
    public Tenant updateTenantStatus(UUID tenantId, TenantStatus status) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + tenantId));
        tenant.setStatus(status);
        return tenantRepository.save(tenant);
    }
}