package com.example.multi_tenant_notifcation_ankit_kathait.provider;

import com.example.multi_tenant_notifcation_ankit_kathait.provider.SmsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SmsProviderTest {

    @InjectMocks
    private SmsProvider smsProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSend() {
        assertDoesNotThrow(() -> smsProvider.send("+1234567890", "Test Message"));
    }
}
