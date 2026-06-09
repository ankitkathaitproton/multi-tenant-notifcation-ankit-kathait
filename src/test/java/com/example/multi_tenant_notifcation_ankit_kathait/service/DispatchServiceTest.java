package com.example.multi_tenant_notifcation_ankit_kathait.service;

import com.example.multi_tenant_notifcation_ankit_kathait.entity.DeliveryAuditLog;
import com.example.multi_tenant_notifcation_ankit_kathait.entity.NotificationDelivery;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import com.example.multi_tenant_notifcation_ankit_kathait.enums.DeliveryStatus;
import com.example.multi_tenant_notifcation_ankit_kathait.provider.NotificationProvider;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.DeliveryAuditLogRepository;
import com.example.multi_tenant_notifcation_ankit_kathait.repository.NotificationDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private NotificationDeliveryRepository deliveryRepository;

    @Mock
    private DeliveryAuditLogRepository auditLogRepository;

    @Mock
    private NotificationProvider mockEmailProvider;

    private DispatchService dispatchService;

    private final UUID deliveryId = UUID.randomUUID();
    private final String recipient = "test@example.com";
    private final String content = "Notification content body";

    @BeforeEach
    void setUp() {
        when(mockEmailProvider.getChannelType()).thenReturn(ChannelType.EMAIL); // <--- THIS IS AN INTERACTION!

        dispatchService = new DispatchService(
                List.of(mockEmailProvider), // <--- Inside here, the stream calls getChannelType()
                deliveryRepository,
                auditLogRepository
        );
    }

    @Nested
    class DispatchMethodTests {

        @Test
        void shouldDispatchSuccessfullyWhenProviderSucceeds() {
            // Arrange
            NotificationDelivery delivery = new NotificationDelivery();
            delivery.setId(deliveryId);
            delivery.setRetryCount(0);
            delivery.setCurrentStatus(DeliveryStatus.SENT);

            when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

            // Act
            dispatchService.dispatch(deliveryId, ChannelType.EMAIL, recipient, content);

            // Assert
            verify(mockEmailProvider, times(1)).send(recipient, content);

            // Verify final state properties
            assertEquals(DeliveryStatus.SENT, delivery.getCurrentStatus());
            verify(deliveryRepository, times(2)).save(delivery); // 1st for PROCESSING, 2nd for SENT
            verify(auditLogRepository, times(2)).save(any(DeliveryAuditLog.class));
        }

        @Test
        void shouldThrowExceptionAndIncrementRetryCountWhenProviderFails() {
            // Arrange
            NotificationDelivery delivery = new NotificationDelivery();
            delivery.setId(deliveryId);
            delivery.setRetryCount(0);
            delivery.setCurrentStatus(DeliveryStatus.SENT);

            when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
            doThrow(new RuntimeException("Connection dropped")).when(mockEmailProvider).send(recipient, content);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    dispatchService.dispatch(deliveryId, ChannelType.EMAIL, recipient, content)
            );

            assertTrue(exception.getMessage().contains("Simulating provider failure"));
            assertEquals(1, delivery.getRetryCount());

            // Verify state progression recorded before crashing out to let Spring Retry kick in
            verify(deliveryRepository, times(2)).save(delivery); // 1st for PROCESSING, 2nd for tracking retry count state change
        }

        @Test
        void shouldThrowExceptionWhenNotificationRecordIsMissing() {
            // Arrange
            when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    dispatchService.dispatch(deliveryId, ChannelType.EMAIL, recipient, content)
            );

            assertTrue(exception.getMessage().contains("NotificationDelivery record not found"));
            verifyNoMoreInteractions(mockEmailProvider);
        }

        @Test
        void shouldThrowExceptionWhenNoProviderMatchesChannelType() {
            // Arrange
            NotificationDelivery delivery = new NotificationDelivery();
            delivery.setId(deliveryId);
            delivery.setRetryCount(0);

            when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

            // Act & Assert
            // Sending via SMS channel which wasn't fed into the constructor provider list mapping
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    dispatchService.dispatch(deliveryId, ChannelType.SMS, recipient, content)
            );

            assertTrue(exception.getMessage().contains("Simulating provider failure"));
            assertEquals(1, delivery.getRetryCount());
        }
    }

    @Nested
    class RecoverMethodTests {

        @Test
        void shouldTransitionToFailedStatusOnRecoveryExecution() {
            // Arrange
            NotificationDelivery delivery = new NotificationDelivery();
            delivery.setId(deliveryId);
            delivery.setCurrentStatus(DeliveryStatus.PROCESSING);
            RuntimeException originalException = new RuntimeException("Final gateway failure timeout");

            when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

            // Act
            dispatchService.recover(originalException, deliveryId, ChannelType.EMAIL, recipient, content);

            // Assert
            assertEquals(DeliveryStatus.FAILED, delivery.getCurrentStatus());
            verify(deliveryRepository, times(1)).save(delivery);

            // Capture the audit logs to verify final error remarks matching
            ArgumentCaptor<DeliveryAuditLog> auditLogCaptor = ArgumentCaptor.forClass(DeliveryAuditLog.class);
            verify(auditLogRepository, times(1)).save(auditLogCaptor.capture());

            DeliveryAuditLog capturedLog = auditLogCaptor.getValue();
            assertEquals(DeliveryStatus.FAILED, capturedLog.getChangedToStatus());
            assertEquals("Exceeded max retry attempts. Final failure.", capturedLog.getRemarks());
        }

        @Test
        void shouldThrowExceptionWhenRecordIsMissingInRecoverMethod() {
            // Arrange
            when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    dispatchService.recover(new RuntimeException("Error"), deliveryId, ChannelType.EMAIL, recipient, content)
            );

            assertTrue(exception.getMessage().contains("NotificationDelivery record not found in recover method"));
        }
    }
}