package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateTemplateRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.dto.NotificationTemplateDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.service.TemplateService;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TemplateResourceTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateResource templateResource;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(templateResource)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    private CreateTemplateRequest createValidRequest() {
        CreateTemplateRequest request = new CreateTemplateRequest();
        request.setName("Welcome Template");
        request.setChannelType(ChannelType.EMAIL);
        request.setContent("Hello text content");
        return request;
    }

    private NotificationTemplate createMockTemplate(UUID tenantId) {
        return NotificationTemplate.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Welcome Template")
                .channelType(ChannelType.EMAIL)
                .content("Hello text content")
                .build();
    }

    // ==========================================
    // POST /api/v1/templates (Create Template)
    // ==========================================
    @Nested
    class CreateTemplateTests {

        @Test
        void shouldCreateTemplateWhenUserIsTenantAdmin() throws Exception {
            UUID tenantId = UUID.randomUUID();
            CreateTemplateRequest request = createValidRequest();
            NotificationTemplate mockSavedTemplate = createMockTemplate(tenantId);

            when(templateService.createTemplate(any(NotificationTemplate.class))).thenReturn(mockSavedTemplate);

            mockMvc.perform(post("/api/v1/templates")
                            .header("X-Tenant-ID", tenantId.toString())
                            .header("X-User-Role", "TENANT_ADMIN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Welcome Template"))
                    .andExpect(jsonPath("$.channelType").value("EMAIL"));
        }

        @Test
        void shouldReturnForbiddenOnCreateWhenUserIsNotTenantAdmin() throws Exception {
            CreateTemplateRequest request = createValidRequest();

            mockMvc.perform(post("/api/v1/templates")
                            .header("X-Tenant-ID", UUID.randomUUID().toString())
                            .header("X-User-Role", "REGULAR_USER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    // ==========================================
    // PUT /api/v1/templates/{templateId} (Update)
    // ==========================================
    @Nested
    class UpdateTemplateTests {

        @Test
        void shouldUpdateTemplateWhenUserIsTenantAdmin() throws Exception {
            UUID tenantId = UUID.randomUUID();
            UUID templateId = UUID.randomUUID();
            CreateTemplateRequest request = createValidRequest();
            NotificationTemplate mockUpdatedTemplate = createMockTemplate(tenantId);

            when(templateService.updateTemplate(eq(templateId), any(NotificationTemplate.class)))
                    .thenReturn(mockUpdatedTemplate);

            mockMvc.perform(put("/api/v1/templates/{templateId}", templateId)
                            .header("X-Tenant-ID", tenantId.toString())
                            .header("X-User-Role", "TENANT_ADMIN")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Welcome Template"));
        }

        @Test
        void shouldReturnForbiddenOnUpdateWhenUserIsNotTenantAdmin() throws Exception {
            UUID templateId = UUID.randomUUID();
            CreateTemplateRequest request = createValidRequest();

            mockMvc.perform(put("/api/v1/templates/{templateId}", templateId)
                            .header("X-Tenant-ID", UUID.randomUUID().toString())
                            .header("X-User-Role", "REGULAR_USER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    // ==========================================
    // GET /api/v1/templates (Get Templates)
    // ==========================================
    @Nested
    class GetTemplatesTests {

        @Test
        void shouldGetTemplatesWhenUserIsTenantAdmin() throws Exception {
            UUID tenantId = UUID.randomUUID();
            NotificationTemplate mockTemplate = createMockTemplate(tenantId);

            when(templateService.getTemplatesByTenant(tenantId)).thenReturn(List.of(mockTemplate));

            mockMvc.perform(get("/api/v1/templates")
                            .header("X-Tenant-ID", tenantId.toString())
                            .header("X-User-Role", "TENANT_ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").value("Welcome Template"));
        }

        @Test
        void shouldReturnEmptyListWhenNoTemplatesFound() throws Exception {
            UUID tenantId = UUID.randomUUID();

            when(templateService.getTemplatesByTenant(tenantId)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/templates")
                            .header("X-Tenant-ID", tenantId.toString())
                            .header("X-User-Role", "TENANT_ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnForbiddenOnGetWhenUserIsNotTenantAdmin() throws Exception {
            mockMvc.perform(get("/api/v1/templates")
                            .header("X-Tenant-ID", UUID.randomUUID().toString())
                            .header("X-User-Role", "REGULAR_USER"))
                    .andExpect(status().isForbidden());
        }
    }
}