package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateService {

    private final NotificationTemplateRepository templateRepository;

    public TemplateService(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(.*?)\\}\\}");

    @Transactional
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        validateTemplateContent(template.getContent());
        return templateRepository.save(template);
    }

    @Transactional
    public NotificationTemplate updateTemplate(UUID templateId, NotificationTemplate updatedTemplate) {
        validateTemplateContent(updatedTemplate.getContent());
        NotificationTemplate existingTemplate = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with ID: " + templateId));
        existingTemplate.setName(updatedTemplate.getName());
        existingTemplate.setChannelType(updatedTemplate.getChannelType());
        existingTemplate.setContent(updatedTemplate.getContent());
        return templateRepository.save(existingTemplate);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByTenant(UUID tenantId) {
        return templateRepository.findByTenantId(tenantId);
    }

    private void validateTemplateContent(String content) {
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            if (matcher.group(1).trim().isEmpty()) {
                throw new IllegalArgumentException("Template variables cannot be empty.");
            }
        }
    }
}