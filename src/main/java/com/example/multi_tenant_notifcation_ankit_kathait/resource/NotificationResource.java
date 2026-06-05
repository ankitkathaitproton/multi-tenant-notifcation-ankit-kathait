package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.NotificationRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationResource {

    private final NotificationService notificationService;

    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestHeader("X-Tenant-ID") UUID tenantId,
                                                 @Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(
                tenantId,
                request.getIdempotencyKey(),
                request.getTemplateId(),
                request.getRecipient(),
                request.getTemplateVariables()
        );
        return ResponseEntity.accepted().build();
    }
}