package com.example.multi_tenant_notifcation_ankit_kathait.interceptor;

import com.example.multi_tenant_notifcation_ankit_kathait.exception.RateLimitExceededException;
import com.example.multi_tenant_notifcation_ankit_kathait.service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    public RateLimitingInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantIdHeader = request.getHeader("X-Tenant-ID");
        if (tenantIdHeader == null) {
            // Allow requests without a tenant ID to proceed
            return true;
        }

        try {
            UUID tenantId = UUID.fromString(tenantIdHeader);
            rateLimitingService.checkRateLimit(tenantId);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Tenant ID format");
            return false;
        } catch (RateLimitExceededException e) {
            response.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            return false;
        }

        return true;
    }
}