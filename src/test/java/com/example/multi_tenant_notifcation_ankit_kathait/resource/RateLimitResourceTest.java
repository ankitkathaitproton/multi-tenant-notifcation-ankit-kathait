package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.UpdateRateLimitRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.TenantRateLimit;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.TenantRateLimitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RateLimitResourceTest {

    @Mock
    private TenantRateLimitRepository tenantRateLimitRepository;

    @InjectMocks
    private RateLimitResource rateLimitResource;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(rateLimitResource)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    private UpdateRateLimitRequest createValidRequest() {
        UpdateRateLimitRequest request = new UpdateRateLimitRequest();
        request.setRequestsPerSecond(100);
        request.setMaxConcurrentJobs(5);
        return request;
    }

    @Nested
    class UpdateRateLimitsTests {

        @Test
        void shouldUpdateRateLimitWhenConfigExistsAndUserIsPlatformAdmin() throws Exception {
            UUID tenantId = UUID.randomUUID();
            UpdateRateLimitRequest request = createValidRequest();

            TenantRateLimit existingRateLimit = new TenantRateLimit(UUID.randomUUID(), tenantId, 10, 1, null);
            TenantRateLimit savedRateLimit = new TenantRateLimit(existingRateLimit.getId(), tenantId, 100, 5, null);

            when(tenantRateLimitRepository.findByTenantId(tenantId)).thenReturn(Optional.of(existingRateLimit));
            when(tenantRateLimitRepository.save(any(TenantRateLimit.class))).thenReturn(savedRateLimit);

            mockMvc.perform(put("/api/v1/admin/tenants/{tenantId}/rate-limits", tenantId)
                            .header("X-User-Role", "PLATFORM_ADMIN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(existingRateLimit.getId().toString()))
                    .andExpect(jsonPath("$.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.requestsPerSecond").value(100))
                    .andExpect(jsonPath("$.maxConcurrentJobs").value(5));
        }

        @Test
        void shouldCreateDefaultRateLimitWhenConfigDoesNotExistAndUserIsPlatformAdmin() throws Exception {
            UUID tenantId = UUID.randomUUID();
            UpdateRateLimitRequest request = createValidRequest();

            // The orElse code block generates a fresh record with an unassigned/null ID
            TenantRateLimit mockSavedNewRateLimit = new TenantRateLimit(UUID.randomUUID(), tenantId, 100, 5, null);

            when(tenantRateLimitRepository.findByTenantId(tenantId)).thenReturn(Optional.empty());
            when(tenantRateLimitRepository.save(any(TenantRateLimit.class))).thenReturn(mockSavedNewRateLimit);

            mockMvc.perform(put("/api/v1/admin/tenants/{tenantId}/rate-limits", tenantId)
                            .header("X-User-Role", "PLATFORM_ADMIN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.requestsPerSecond").value(100))
                    .andExpect(jsonPath("$.maxConcurrentJobs").value(5));
        }

        @Test
        void shouldReturnForbiddenWhenUserIsNotPlatformAdmin() throws Exception {
            UUID tenantId = UUID.randomUUID();
            UpdateRateLimitRequest request = createValidRequest();

            mockMvc.perform(put("/api/v1/admin/tenants/{tenantId}/rate-limits", tenantId)
                            .header("X-User-Role", "TENANT_ADMIN") // Wrong access tier role
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }
}