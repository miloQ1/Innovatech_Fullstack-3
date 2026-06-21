package com.example.ms_archivos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsArchivosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsArchivosApplication.class, args);
    }
}
