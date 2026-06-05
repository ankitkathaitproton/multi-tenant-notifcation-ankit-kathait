package com.example.multi_tenant_notifcation_ankit_kathait.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

    @Value("${notification.dispatch.core-pool-size}")
    private int corePoolSize;

    @Value("${notification.dispatch.max-pool-size}")
    private int maxPoolSize;

    @Value("${notification.dispatch.queue-capacity}")
    private int queueCapacity;

    @Bean("notificationTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("NotifDispatch-");
        executor.initialize();
        return executor;
    }
}