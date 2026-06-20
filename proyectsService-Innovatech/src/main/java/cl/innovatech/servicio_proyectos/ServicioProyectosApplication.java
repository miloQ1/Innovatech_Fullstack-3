package cl.innovatech.servicio_proyectos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServicioProyectosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioProyectosApplication.class, args);
	}

}
