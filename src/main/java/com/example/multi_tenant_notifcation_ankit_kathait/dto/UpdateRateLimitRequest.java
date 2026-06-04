package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRateLimitRequest {

    @NotNull(message = "Requests per second cannot be null")
    private Integer requestsPerSecond;

    @NotNull(message = "Max concurrent jobs cannot be null")
    private Integer maxConcurrentJobs;
}