package cl.duoc.bffGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BffGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffGatewayApplication.class, args);
	}

}
