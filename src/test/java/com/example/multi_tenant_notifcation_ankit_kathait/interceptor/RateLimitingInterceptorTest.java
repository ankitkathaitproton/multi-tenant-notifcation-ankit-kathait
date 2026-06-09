package com.example.multi_tenant_notifcation_ankit_kathait.interceptor;

import com.example.multi_tenant_notifcation_ankit_kathait.service.RateLimitingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RateLimitingInterceptorTest {

    @Mock
    private RateLimitingService rateLimitingService;

    @InjectMocks
    private RateLimitingInterceptor rateLimitingInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPreHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", UUID.randomUUID().toString());
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitingInterceptor.preHandle(request, response, new Object());

        verify(rateLimitingService, times(1)).checkRateLimit(any(UUID.class));
    }
}