package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateTenantRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.Tenant;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.TenantStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TenantResourceTest {

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TenantResource tenantResource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tenantResource).build();
    }

    @Test
    void testCreateTenant() throws Exception {
        when(tenantService.createTenant(any())).thenReturn(new Tenant(UUID.randomUUID(), "tenant-1", TenantStatus.ACTIVE, Instant.now(), Instant.now()));
        mockMvc.perform(post("/api/v1/admin/tenants")
                        .header("X-User-Role", "PLATFORM_ADMIN") // Add required header for authorization
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"tenant-1\"}"))
                .andExpect(status().isCreated()); // Expect 201 Created
    }
}