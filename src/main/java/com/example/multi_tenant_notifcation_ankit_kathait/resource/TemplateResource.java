package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateTemplateRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.dto.NotificationTemplateDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateResource {

    private final TemplateService templateService;

    public TemplateResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<NotificationTemplateDTO> createTemplate(@RequestHeader("X-Tenant-ID") UUID tenantId,
                                                                  @RequestHeader("X-User-Role") String userRole,
                                                                  @Valid @RequestBody CreateTemplateRequest request) {
        if (!"TENANT_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        NotificationTemplate newTemplate = NotificationTemplate.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .channelType(request.getChannelType())
                .content(request.getContent())
                .build();

        NotificationTemplate createdTemplate = templateService.createTemplate(newTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NotificationTemplateDTO(createdTemplate));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<NotificationTemplateDTO> updateTemplate(@RequestHeader("X-Tenant-ID") UUID tenantId,
                                                                  @RequestHeader("X-User-Role") String userRole,
                                                                  @PathVariable UUID templateId,
                                                                  @Valid @RequestBody CreateTemplateRequest request) {
        if (!"TENANT_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        NotificationTemplate updatedTemplateData = NotificationTemplate.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .channelType(request.getChannelType())
                .content(request.getContent())
                .build();

        NotificationTemplate updatedTemplate = templateService.updateTemplate(templateId, updatedTemplateData);
        return ResponseEntity.ok(new NotificationTemplateDTO(updatedTemplate));
    }

    @GetMapping
    public ResponseEntity<List<NotificationTemplateDTO>> getTemplates(@RequestHeader("X-Tenant-ID") UUID tenantId,
                                                                      @RequestHeader("X-User-Role") String userRole) {
        if (!"TENANT_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<NotificationTemplate> templates = templateService.getTemplatesByTenant(tenantId);
        List<NotificationTemplateDTO> templateDTOs = templates.stream()
                .map(NotificationTemplateDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(templateDTOs);
    }
}