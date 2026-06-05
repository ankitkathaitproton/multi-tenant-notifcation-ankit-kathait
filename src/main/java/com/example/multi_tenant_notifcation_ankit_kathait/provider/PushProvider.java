package com.example.multi_tenant_notifcation_ankit_kathait.provider;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushProvider implements NotificationProvider {

    private static final Logger logger = LoggerFactory.getLogger(PushProvider.class);

    @Override
    public void send(String recipient, String content) {
        logger.info("Sending PUSH to {}: {}", recipient, content);
        // Simulate a failure for retry demonstration
        if (Math.random() > 0.5) {
            throw new RuntimeException("Failed to send push notification");
        }
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.PUSH;
    }
}