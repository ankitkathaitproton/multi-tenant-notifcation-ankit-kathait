package com.example.multi_tenant_notifcation_ankit_kathait.repository;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationDelivery;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
    Optional<NotificationDelivery> findByIdempotencyKeyAndTenantId(String idempotencyKey, UUID tenantId);

    Page<NotificationDelivery> findByTenantIdAndCurrentStatus(UUID tenantId, DeliveryStatus status, Pageable pageable);

    Page<NotificationDelivery> findByTenantId(UUID tenantId, Pageable pageable);
}