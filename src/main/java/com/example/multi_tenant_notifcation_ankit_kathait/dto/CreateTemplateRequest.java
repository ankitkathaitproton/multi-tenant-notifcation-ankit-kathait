package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTemplateRequest {
    @NotBlank(message = "Template name cannot be empty")
    private String name;

    @NotNull(message = "Channel type cannot be null")
    private ChannelType channelType;

    @NotBlank(message = "Content cannot be blank")
    private String content;
}