package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.ChannelConfig;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelConfigDTO {
    private UUID id;
    private ChannelType type;
    private boolean isActive;

    public ChannelConfigDTO(ChannelConfig config) {
        this.id = config.getId();
        this.type = config.getType();
        this.isActive = config.getIsActive();
    }
}