package com.itplh.hero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(processors);
        threadPoolTaskExecutor.setMaxPoolSize(processors + 1);
        threadPoolTaskExecutor.setKeepAliveSeconds(0);
        threadPoolTaskExecutor.setQueueCapacity(0);
        threadPoolTaskExecutor.setThreadNamePrefix("hero-thread-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
