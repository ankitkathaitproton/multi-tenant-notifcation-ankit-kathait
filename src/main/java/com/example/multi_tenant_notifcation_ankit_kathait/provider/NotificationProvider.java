package com.example.multi_tenant_notifcation_ankit_kathait.provider;

import com.example.multi_tenant_notifcation_ankit_kathait.enums.ChannelType;

public interface NotificationProvider {
    void send(String recipient, String content);
    ChannelType getChannelType();
}