package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.Tenant;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.TenantStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTenant() {
        String tenantName = "test-tenant";
        when(tenantRepository.existsByName(tenantName)).thenReturn(false);

        tenantService.createTenant(tenantName);

        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    void testGetTenantById() {
        UUID tenantId = UUID.randomUUID();
        tenantService.getTenantById(tenantId);
        verify(tenantRepository, times(1)).findById(tenantId);
    }

    @Test
    void testUpdateTenantStatus() {
        UUID tenantId = UUID.randomUUID();
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setStatus(TenantStatus.ACTIVE);

        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        tenantService.updateTenantStatus(tenantId, TenantStatus.SUSPENDED);

        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }
}