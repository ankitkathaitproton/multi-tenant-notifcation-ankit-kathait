package com.example.multi_tenant_notifcation_ankit_kathait.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.time.Instant;

import java.util.UUID;

@Entity
@Table(name = "tenant_rate_limit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantRateLimit {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "requests_per_second")
    private Integer requestsPerSecond;

    @Column(name = "max_concurrent_jobs")
    private Integer maxConcurrentJobs;

    @Column(name = "last_request_at")
    private Instant lastRequestAt;
}