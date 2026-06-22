package com.example.ms_recursos_innovatech.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AdminWriteFilter> adminWriteFilterRegistration() {
        FilterRegistrationBean<AdminWriteFilter> registration = new FilterRegistrationBean<>(new AdminWriteFilter());
        registration.addUrlPatterns("/api/professionals/*");
        return registration;
    }
}
