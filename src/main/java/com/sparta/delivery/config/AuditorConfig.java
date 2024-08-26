package com.sparta.delivery.config;

import com.sparta.delivery.common.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl(); // 단일 정의
    }
}
