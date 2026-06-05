package com.example.multi_tenant_notifcation_ankit_kathait.entity;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class DeliveryAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_delivery_id", nullable = false)
    private NotificationDelivery notificationDelivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus changedToStatus;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    private Instant timestamp;
}