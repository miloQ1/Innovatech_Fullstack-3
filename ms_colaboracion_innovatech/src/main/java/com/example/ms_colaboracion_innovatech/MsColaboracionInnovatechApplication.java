package com.example.ms_colaboracion_innovatech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsColaboracionInnovatechApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsColaboracionInnovatechApplication.class, args);
	}

}
