package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.ChannelConfigDTO;
import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateChannelConfigRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.ChannelConfig;
import com.example.multi_tenant_notifcation_ankit_kathait.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelResource {

    private final ChannelService channelService;

    public ChannelResource(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public ResponseEntity<ChannelConfigDTO> createChannel(@RequestHeader("X-Tenant-ID") UUID tenantId,
                                                          @RequestHeader("X-User-Role") String userRole,
                                                          @Valid @RequestBody CreateChannelConfigRequest request) {
        if (!"TENANT_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ChannelConfig newChannel = ChannelConfig.builder()
                .tenantId(tenantId)
                .type(request.getType())
                .encryptedCredentials(request.getEncryptedCredentials())
                .isActive(request.getIsActive())
                .build();

        ChannelConfig createdChannel = channelService.createChannel(newChannel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChannelConfigDTO(createdChannel));
    }

    @GetMapping
    public ResponseEntity<List<ChannelConfigDTO>> getChannels(@RequestHeader("X-Tenant-ID") UUID tenantId,
                                                              @RequestHeader("X-User-Role") String userRole) {
        if (!"TENANT_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ChannelConfig> channels = channelService.getChannelsByTenant(tenantId);
        List<ChannelConfigDTO> channelDTOs = channels.stream()
                .map(ChannelConfigDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(channelDTOs);
    }
}