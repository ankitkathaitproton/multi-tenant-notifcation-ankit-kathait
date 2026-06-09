package com.example.multi_tenant_notifcation_ankit_kathait.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailProviderTest {

    @InjectMocks
    private EmailProvider emailProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSend() {
        assertDoesNotThrow(() -> emailProvider.send("test@example.com", "Test Body"));
    }
}