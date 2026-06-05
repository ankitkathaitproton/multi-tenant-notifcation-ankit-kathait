package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class DeliveryReportDTO {
    private UUID notificationId;
    private ChannelType channelType;
    private String recipient;
    private DeliveryStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public DeliveryReportDTO(UUID notificationId, ChannelType channelType, String recipient, DeliveryStatus status, Instant createdAt, Instant updatedAt) {
        this.notificationId = notificationId;
        this.channelType = channelType;
        this.recipient = recipient;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
