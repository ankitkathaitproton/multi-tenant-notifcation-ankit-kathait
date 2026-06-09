package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateTemplateRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TemplateServiceTest {

    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;

    @InjectMocks
    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTemplate() {
        NotificationTemplate template = new NotificationTemplate();
        template.setContent("Hello, {{name}}");
        // set other properties of template

        templateService.createTemplate(template);

        verify(notificationTemplateRepository, times(1)).save(any(NotificationTemplate.class));
    }

    @Test
    void testUpdateTemplate() {
        UUID templateId = UUID.randomUUID();
        NotificationTemplate updatedTemplate = new NotificationTemplate();
        updatedTemplate.setContent("Hello, {{newName}}");
        // set other properties of updatedTemplate

        when(notificationTemplateRepository.findById(templateId)).thenReturn(Optional.of(new NotificationTemplate()));

        templateService.updateTemplate(templateId, updatedTemplate);

        verify(notificationTemplateRepository, times(1)).save(any(NotificationTemplate.class));
    }

    @Test
    void testGetTemplatesByTenant() {
        UUID tenantId = UUID.randomUUID();
        templateService.getTemplatesByTenant(tenantId);
        verify(notificationTemplateRepository, times(1)).findByTenantId(tenantId);
    }
}