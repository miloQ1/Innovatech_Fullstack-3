package com.example.ms_recursos_innovatech.config;

import com.example.ms_recursos_innovatech.model.Professional;
import com.example.ms_recursos_innovatech.repository.ProfessionalRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminProfessionalInitializer {

    public static final String ADMIN_EMPLOYEE_CODE = "USR-ADMIN-0001";

    @Bean
    CommandLineRunner seedAdminProfessional(ProfessionalRepository professionalRepository) {
        return args -> {
            if (professionalRepository.findByEmployeeCode(ADMIN_EMPLOYEE_CODE).isPresent()) {
                return;
            }

            Professional admin = new Professional();
            admin.setEmployeeCode(ADMIN_EMPLOYEE_CODE);
            admin.setFirstName("Administrador");
            admin.setLastName("Innovatech");
            admin.setEmail("admin@innovatech.cl");
            admin.setRoleName("Administrador de plataforma");
            admin.setSeniority("LEAD");
            admin.setLocation("Santiago");
            admin.setTimeZone("America/Santiago");
            admin.setWeeklyCapacityHours(40);
            admin.setStatus("ACTIVE");
            LocalDateTime now = LocalDateTime.now();
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);

            professionalRepository.save(admin);
        };
    }
}
