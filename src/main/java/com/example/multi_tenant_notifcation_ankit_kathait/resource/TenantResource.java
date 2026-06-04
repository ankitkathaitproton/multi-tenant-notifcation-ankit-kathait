package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateTenantRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.dto.TenantDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.dto.UpdateTenantStatusRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.Tenant;
import com.example.multi_tenant_notifcation_ankit_kathait.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/tenants")
public class TenantResource {

    private final TenantService tenantService;

    public TenantResource(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(@RequestHeader("X-User-Role") String userRole,
                                                  @Valid @RequestBody CreateTenantRequest request) {
        if (!"PLATFORM_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Tenant tenant = tenantService.createTenant(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TenantDTO(tenant));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantDTO> getTenant(@RequestHeader("X-User-Role") String userRole,
                                               @PathVariable UUID tenantId) {
        if (!"PLATFORM_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return tenantService.getTenantById(tenantId)
                .map(tenant -> ResponseEntity.ok(new TenantDTO(tenant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{tenantId}/status")
    public ResponseEntity<TenantDTO> updateTenantStatus(@RequestHeader("X-User-Role") String userRole,
                                                        @PathVariable UUID tenantId,
                                                        @Valid @RequestBody UpdateTenantStatusRequest request) {
        if (!"PLATFORM_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Tenant updatedTenant = tenantService.updateTenantStatus(tenantId, request.getStatus());
        return ResponseEntity.ok(new TenantDTO(updatedTenant));
    }
}