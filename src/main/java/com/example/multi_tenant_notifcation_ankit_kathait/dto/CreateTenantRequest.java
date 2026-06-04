package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTenantRequest {
    @NotBlank(message = "Tenant name cannot be empty")
    private String name;
}