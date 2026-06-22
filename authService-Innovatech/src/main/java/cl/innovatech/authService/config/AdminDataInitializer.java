package cl.innovatech.authService.config;

import cl.innovatech.authService.model.User;
import cl.innovatech.authService.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminDataInitializer {

    public static final String ADMIN_ID = "USR-ADMIN-0001";
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_EMAIL = "admin@innovatech.cl";

    @Bean
    CommandLineRunner seedAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsByUserName(ADMIN_USERNAME) || userRepository.existsByEmail(ADMIN_EMAIL)) {
                return;
            }

            User admin = new User();
            admin.setId(ADMIN_ID);
            admin.setUserName(ADMIN_USERNAME);
            admin.setFirstName("Administrador");
            admin.setLastName("Innovatech");
            admin.setEmail(ADMIN_EMAIL);
            admin.setPasswordHash(passwordEncoder.encode("Admin12345"));
            admin.setStatus("ACTIVE");
            admin.setRole("ADMIN");
            admin.setEnabled(true);

            userRepository.save(admin);
        };
    }
}
