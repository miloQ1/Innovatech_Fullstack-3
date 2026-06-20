package com.example.ms_recursos_innovatech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsRecursosInnovatechApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRecursosInnovatechApplication.class, args);
	}

}
