package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.DeliveryReportDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportResourceTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReportResource reportResource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(reportResource)
                // Fixes the 1st error: Resolves Pageable parameters
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                // Fixes the 2nd error: Registers a fresh JSON converter for the response body
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void testGetDeliveryReports_Success() throws Exception {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        Page<DeliveryReportDTO> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);

        when(notificationService.getDeliveryReports(eq(tenantId), any(Optional.class), any()))
                .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/deliveries")
                        .header("X-Tenant-ID", tenantId.toString())
                        .header("X-User-Role", "TENANT_ADMIN")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testGetDeliveryReports_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/reports/deliveries")
                        .header("X-Tenant-ID", UUID.randomUUID().toString())
                        .header("X-User-Role", "REGULAR_USER"))
                .andExpect(status().isForbidden());
    }
}