package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class HealthCheckResponse {
    private String status;
    private LocalDateTime serverTime;
    private LocalDateTime databaseTime;
    private String message;
}