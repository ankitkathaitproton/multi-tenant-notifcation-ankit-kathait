package com.example.multi_tenant_notifcation_ankit_kathait.repository;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.ScheduledNotification;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ScheduledNotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotification, UUID> {

    List<ScheduledNotification> findByTenantIdAndStatus(UUID tenantId, ScheduledNotificationStatus status);

    List<ScheduledNotification> findByScheduledTimeBeforeAndStatus(Instant time, ScheduledNotificationStatus status);
}

