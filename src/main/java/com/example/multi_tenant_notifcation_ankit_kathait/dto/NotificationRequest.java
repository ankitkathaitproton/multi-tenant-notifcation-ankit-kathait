package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class NotificationRequest {
    @NotNull
    private UUID templateId;

    @NotBlank
    private String recipient;

    private Map<String, String> templateVariables;

    @NotBlank
    private String idempotencyKey;
}