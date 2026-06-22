package com.example.ms_recursos_innovatech.config;

import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AdminWriteFilter> adminWriteFilterRegistration(ProfessionalRepository professionalRepository) {
        FilterRegistrationBean<AdminWriteFilter> registration = new FilterRegistrationBean<>(new AdminWriteFilter(professionalRepository));
        registration.addUrlPatterns("/api/professionals", "/api/professionals/*");
        return registration;
    }
}
