package com.example.multi_tenant_notifcation_ankit_kathait.resource;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.CreateChannelConfigRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.ChannelConfig;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.service.ChannelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChannelResourceTest {

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private ChannelResource channelResource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(channelResource).build();
    }

    @Test
    void testCreateChannel() throws Exception {
        CreateChannelConfigRequest request = new CreateChannelConfigRequest();
        request.setType(ChannelType.EMAIL);
        request.setEncryptedCredentials("test-credentials");
        request.setIsActive(true);

        UUID tenantId = UUID.randomUUID();
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setId(UUID.randomUUID());
        channelConfig.setTenantId(tenantId);
        channelConfig.setType(request.getType());
        channelConfig.setEncryptedCredentials(request.getEncryptedCredentials());
        channelConfig.setIsActive(request.getIsActive());

        Mockito.when(channelService.createChannel(any())).thenReturn(channelConfig);

        mockMvc.perform(post("/api/v1/channels")
                        .header("X-Tenant-ID", tenantId.toString())
                        .header("X-User-Role", "TENANT_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}