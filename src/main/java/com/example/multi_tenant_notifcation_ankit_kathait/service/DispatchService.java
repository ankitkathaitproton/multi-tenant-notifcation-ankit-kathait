package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.DeliveryAuditLog;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationDelivery;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.provider.NotificationProvider;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.DeliveryAuditLogRepository;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DispatchService {

    private static final Logger logger = LoggerFactory.getLogger(DispatchService.class);
    private final Map<ChannelType, NotificationProvider> providerMap;
    private final NotificationDeliveryRepository deliveryRepository;
    private final DeliveryAuditLogRepository auditLogRepository;

    public DispatchService(List<NotificationProvider> providers, NotificationDeliveryRepository deliveryRepository, DeliveryAuditLogRepository auditLogRepository) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(NotificationProvider::getChannelType, Function.identity()));
        this.deliveryRepository = deliveryRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Async("notificationTaskExecutor")
    @Retryable(
            value = {RuntimeException.class},
            maxAttemptsExpression = "${notification.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${notification.retry.delay-ms}")
    )
    @Transactional
    public void dispatch(UUID deliveryId, ChannelType channelType, String recipient, String content) {
        NotificationDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalStateException("NotificationDelivery record not found: " + deliveryId));

        logger.info("Dispatching notification {} via {}", delivery.getId(), channelType);
        updateStatus(delivery, DeliveryStatus.PROCESSING, "Dispatch attempt " + (delivery.getRetryCount() + 1));

        try {
            NotificationProvider provider = providerMap.get(channelType);
            if (provider == null) {
                throw new IllegalStateException("No provider found for channel type: " + channelType);
            }
            provider.send(recipient, content);
            updateStatus(delivery, DeliveryStatus.SENT, "Successfully sent");
        } catch (Exception e) {
            logger.error("Failed to send notification {}", delivery.getId(), e);
            delivery.setRetryCount(delivery.getRetryCount() + 1);
            deliveryRepository.save(delivery);
            throw new RuntimeException("Simulating provider failure", e); // Re-throw to trigger retry
        }
    }

    @Recover
    @Transactional
    public void recover(RuntimeException e, UUID deliveryId, ChannelType channelType, String recipient, String content) {
        logger.error("Dispatch failed for notification {} after multiple retries. Moving to DLQ.", deliveryId, e);
        NotificationDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalStateException("NotificationDelivery record not found in recover method: " + deliveryId));
        updateStatus(delivery, DeliveryStatus.FAILED, "Exceeded max retry attempts. Final failure.");
    }

    private void updateStatus(NotificationDelivery delivery, DeliveryStatus status, String remarks) {
        delivery.setCurrentStatus(status);
        deliveryRepository.save(delivery);

        DeliveryAuditLog auditLog = new DeliveryAuditLog();
        auditLog.setNotificationDelivery(delivery);
        auditLog.setChangedToStatus(status);
        auditLog.setRemarks(remarks);
        auditLogRepository.save(auditLog);
    }
}