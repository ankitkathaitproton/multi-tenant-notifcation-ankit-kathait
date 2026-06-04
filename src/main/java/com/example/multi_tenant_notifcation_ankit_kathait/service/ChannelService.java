package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.ChannelConfig;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.ChannelConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChannelService {

    private final ChannelConfigRepository channelConfigRepository;

    public ChannelService(ChannelConfigRepository channelConfigRepository) {
        this.channelConfigRepository = channelConfigRepository;
    }

    @Transactional
    public ChannelConfig createChannel(ChannelConfig channelConfig) {
        return channelConfigRepository.save(channelConfig);
    }

    @Transactional(readOnly = true)
    public List<ChannelConfig> getChannelsByTenant(UUID tenantId) {
        return channelConfigRepository.findByTenantId(tenantId);
    }
}