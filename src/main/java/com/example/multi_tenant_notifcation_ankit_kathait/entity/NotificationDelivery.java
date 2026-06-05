package com.example.multi_tenant_notifcation_ankit_kathait.entity;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class NotificationDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType;

    @Column(nullable = false)
    private String recipient;

    @Column(columnDefinition = "TEXT")
    private String renderedContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus currentStatus;

    private Integer retryCount = 0;

    @Column(unique = true)
    private String idempotencyKey;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}