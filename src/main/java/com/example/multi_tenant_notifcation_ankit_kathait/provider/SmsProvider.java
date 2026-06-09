package com.example.multi_tenant_notifcation_ankit_kathait.provider;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsProvider implements NotificationProvider {

    private static final Logger logger = LoggerFactory.getLogger(SmsProvider.class);

    @Override
    public void send(String recipient, String content) {
        logger.info("Sending SMS to {}: {}", recipient, content);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.SMS;
    }
}