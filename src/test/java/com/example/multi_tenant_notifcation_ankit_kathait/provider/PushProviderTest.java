package com.example.multi_tenant_notifcation_ankit_kathait.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PushProviderTest {

    @InjectMocks
    private PushProvider pushProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSend() {
        assertDoesNotThrow(() -> {
            pushProvider.send("device-token", "Test Body");
        });
    }
}