package com.example.multi_tenant_notifcation_ankit_kathait.repository;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {

    @Query(value = "SELECT NOW()", nativeQuery = true)
    Timestamp getDatabaseTime();
}
