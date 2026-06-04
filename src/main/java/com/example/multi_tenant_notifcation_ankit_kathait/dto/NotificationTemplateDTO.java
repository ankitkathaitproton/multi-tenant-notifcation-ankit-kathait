package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import lombok.Data;

import java.util.UUID;

@Data
public class NotificationTemplateDTO {
    private UUID id;
    private String name;
    private ChannelType channelType;
    private String content;

    public NotificationTemplateDTO(NotificationTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.channelType = template.getChannelType();
        this.content = template.getContent();
    }
}