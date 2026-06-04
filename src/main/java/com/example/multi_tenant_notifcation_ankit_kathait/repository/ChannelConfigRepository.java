package com.example.multi_tenant_notifcation_ankit_kathait.repository;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.ChannelConfig;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelConfigRepository extends JpaRepository<ChannelConfig, UUID> {

    List<ChannelConfig> findByTenantId(UUID tenantId);

    List<ChannelConfig> findByTenantIdAndType(UUID tenantId, ChannelType type);
}

