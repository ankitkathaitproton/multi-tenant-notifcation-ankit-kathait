package com.example.multi_tenant_notifcation_ankit_kathait.entity;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ScheduledNotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "scheduled_notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledNotification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "template_id", nullable = false)
    private UUID templateId;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "template_variables_json", columnDefinition = "text")
    private String templateVariablesJson;

    @Column(name = "scheduled_time")
    private Instant scheduledTime;

    @Column(name = "status")
    private ScheduledNotificationStatus status;
}

