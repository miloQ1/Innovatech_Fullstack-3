package cl.innovatech.servicio_proyectos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityBeansConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("No UserDetailsService activo");
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}