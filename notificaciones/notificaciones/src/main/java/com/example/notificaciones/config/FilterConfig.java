package com.example.notificaciones.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AdminRoleFilter> adminRoleFilterRegistration() {
        FilterRegistrationBean<AdminRoleFilter> registration = new FilterRegistrationBean<>(new AdminRoleFilter());
        registration.addUrlPatterns("/api/templates", "/api/templates/*");
        return registration;
    }
}
