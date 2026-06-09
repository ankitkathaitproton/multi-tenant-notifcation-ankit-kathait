package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.dto.NotificationRequest;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationDelivery;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationTemplate;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationDeliveryRepository;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationTemplateRepository templateRepository;
    @Mock
    private NotificationDeliveryRepository deliveryRepository;
    @Mock
    private DispatchService dispatchService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification() {
        UUID tenantId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        String idempotencyKey = UUID.randomUUID().toString();
        String recipient = "test@example.com";
        Map<String, String> variables = Map.of("name", "Test");

        NotificationTemplate template = new NotificationTemplate();
        template.setTenantId(tenantId);
        template.setContent("Hello, {{name}}");
        template.setChannelType(ChannelType.EMAIL);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(deliveryRepository.findByIdempotencyKeyAndTenantId(anyString(), any(UUID.class))).thenReturn(Optional.empty());

        // Ensure the saved entity has an ID
        when(deliveryRepository.save(any(NotificationDelivery.class))).thenAnswer(invocation -> {
            NotificationDelivery delivery = invocation.getArgument(0);
            delivery.setId(UUID.randomUUID()); // Set a non-null ID
            return delivery;
        });

        notificationService.sendNotification(tenantId, idempotencyKey, templateId, recipient, variables);

        verify(deliveryRepository, times(1)).save(any(NotificationDelivery.class));
        verify(dispatchService, times(1)).dispatch(any(UUID.class), any(ChannelType.class), anyString(), anyString());
    }

    @Test
    void testGetDeliveryReports() {
        UUID tenantId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        when(deliveryRepository.findByTenantId(any(UUID.class), any(Pageable.class))).thenReturn(Page.empty());
        notificationService.getDeliveryReports(tenantId, Optional.empty(), pageable);
        verify(deliveryRepository, times(1)).findByTenantId(tenantId, pageable);
    }

    @Test
    void testGetNotificationStatus() {
        UUID tenantId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setTenantId(tenantId);
        delivery.setCurrentStatus(DeliveryStatus.SENT);

        when(deliveryRepository.findById(notificationId)).thenReturn(Optional.of(delivery));

        String status = notificationService.getNotificationStatus(tenantId, notificationId);

        assertEquals("SENT", status);
    }
}