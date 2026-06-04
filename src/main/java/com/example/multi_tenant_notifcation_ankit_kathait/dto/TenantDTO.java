package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.Tenant;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.TenantStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TenantDTO {
    private UUID id;
    private String name;
    private TenantStatus status;
    private Instant createdAt;

    public TenantDTO(Tenant tenant) {
        this.id = tenant.getId();
        this.name = tenant.getName();
        this.status = tenant.getStatus();
        this.createdAt = tenant.getCreatedAt();
    }
}