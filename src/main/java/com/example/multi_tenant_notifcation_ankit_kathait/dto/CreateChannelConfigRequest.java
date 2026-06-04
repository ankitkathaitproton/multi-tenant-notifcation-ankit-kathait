package com.example.multi_tenant_notifcation_ankit_kathait.dto;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateChannelConfigRequest {
    @NotNull(message = "Channel type cannot be null")
    private ChannelType type;

    @NotBlank(message = "Credentials cannot be blank")
    private String encryptedCredentials;

    @NotNull(message = "isActive cannot be null")
    private Boolean isActive;
}