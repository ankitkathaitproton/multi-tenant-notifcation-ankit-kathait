package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.DeliveryReportDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationDelivery;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationDeliveryRepository;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationTemplateRepository;
import com.example.multi_tenant_notifcation_ankit_kathait.service.DispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final DispatchService dispatchService;

    public NotificationService(NotificationTemplateRepository templateRepository, NotificationDeliveryRepository deliveryRepository, DispatchService dispatchService) {
        this.templateRepository = templateRepository;
        this.deliveryRepository = deliveryRepository;
        this.dispatchService = dispatchService;
    }

    @Transactional
    public void sendNotification(UUID tenantId, String idempotencyKey, UUID templateId, String recipient, Map<String, String> variables) {
        // 1. Idempotency Check
        Optional<NotificationDelivery> existingDelivery = deliveryRepository.findByIdempotencyKeyAndTenantId(idempotencyKey, tenantId);
        if (existingDelivery.isPresent()) {
            log.warn("Duplicate notification request received with idempotency key: {}", idempotencyKey);
            return; // Or throw an exception, depending on desired behavior
        }

        // 2. Fetch Template
        NotificationTemplate template = templateRepository.findById(templateId)
                .filter(t -> t.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found or does not belong to tenant"));

        // 3. Render Content
        String content = renderTemplate(template.getContent(), variables);

        // 4. Create and Persist Delivery Record
        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setTenantId(tenantId);
        delivery.setIdempotencyKey(idempotencyKey);
        delivery.setChannelType(template.getChannelType());
        delivery.setRecipient(recipient);
        delivery.setRenderedContent(content);
        delivery.setCurrentStatus(DeliveryStatus.QUEUED);
        deliveryRepository.save(delivery);

        // 5. Dispatch for Sending
        dispatchService.dispatch(delivery.getId(), template.getChannelType(), recipient, content);
    }

    public Page<DeliveryReportDTO> getDeliveryReports(UUID tenantId, Optional<DeliveryStatus> status, Pageable pageable) {
        Page<NotificationDelivery> deliveries;
        if (status.isPresent()) {
            deliveries = deliveryRepository.findByTenantIdAndCurrentStatus(tenantId, status.get(), pageable);
        } else {
            deliveries = deliveryRepository.findByTenantId(tenantId, pageable);
        }

        return deliveries.map(delivery -> new DeliveryReportDTO(
                delivery.getId(),
                delivery.getChannelType(),
                delivery.getRecipient(),
                delivery.getCurrentStatus(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        ));
    }

    private String renderTemplate(String content, Map<String, String> variables) {
        if (variables == null) {
            return content;
        }
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return content;
    }

    public String getNotificationStatus(UUID tenantId, UUID notificationId) {
        return deliveryRepository.findById(notificationId)
                .filter(delivery -> delivery.getTenantId().equals(tenantId))
                .map(delivery -> delivery.getCurrentStatus().toString())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found or does not belong to tenant"));
    }
}