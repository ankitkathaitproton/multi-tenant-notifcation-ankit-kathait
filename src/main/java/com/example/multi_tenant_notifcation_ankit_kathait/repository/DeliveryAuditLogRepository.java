package com.example.multi_tenant_notifcation_ankit_kathait.repository;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.DeliveryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryAuditLogRepository extends JpaRepository<DeliveryAuditLog, UUID> {
}
