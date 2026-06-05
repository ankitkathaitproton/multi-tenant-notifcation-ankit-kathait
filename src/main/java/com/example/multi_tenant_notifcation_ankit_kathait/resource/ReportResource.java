package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.DeliveryReportDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportResource {

    private final NotificationService notificationService;

    public ReportResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/deliveries")
    public ResponseEntity<Page<DeliveryReportDTO>> getDeliveryReports(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(required = false) DeliveryStatus status,
            Pageable pageable) {

        if (!"TENANT_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<DeliveryReportDTO> reports = notificationService.getDeliveryReports(tenantId, Optional.ofNullable(status), pageable);
        return ResponseEntity.ok(reports);
    }
}