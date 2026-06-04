package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String status;
    private LocalDateTime timestamp;
    private String error;
    private String details;
}
