package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.TenantStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTenantStatusRequest {
    @NotNull(message = "Status cannot be null")
    private TenantStatus status;
}