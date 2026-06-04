package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.HealthCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.ErrorResponse;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.HealthCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor // Lombok handles constructor injection automatically for final fields
public class HealthCheckResource {

    private final HealthCheckRepository healthCheckRepository;

    @GetMapping
    public ResponseEntity<?> checkHealth() {
        try {
            // Using Spring Data JPA native query call
            Timestamp dbTimestamp = healthCheckRepository.getDatabaseTime();

            LocalDateTime databaseTime = dbTimestamp.toLocalDateTime();
            LocalDateTime serverTime = LocalDateTime.now();

            HealthCheckResponse successResponse = HealthCheckResponse.builder()
                    .status("UP")
                    .serverTime(serverTime)
                    .databaseTime(databaseTime)
                    .message("Successfully connected to the database schema: tenant_notification")
                    .build();

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            // Returning the dedicated Exception DTO on failure
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status("DOWN")
                    .timestamp(LocalDateTime.now())
                    .error("Database connection failed")
                    .details(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}