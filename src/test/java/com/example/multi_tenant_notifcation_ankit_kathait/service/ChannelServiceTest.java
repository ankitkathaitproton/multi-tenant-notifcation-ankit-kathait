package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateChannelConfigRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.ChannelConfig;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.ChannelConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ChannelServiceTest {

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    @InjectMocks
    private ChannelService channelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateChannelConfig() {
        ChannelConfig channelConfig = new ChannelConfig();
        // set properties of channelConfig

        channelService.createChannel(channelConfig);

        verify(channelConfigRepository, times(1)).save(any(ChannelConfig.class));
    }

    @Test
    void testGetChannelsByTenant() {
        UUID tenantId = UUID.randomUUID();
        channelService.getChannelsByTenant(tenantId);
        verify(channelConfigRepository, times(1)).findByTenantId(tenantId);
    }
}