package com.cmcorg.engine.authnacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthNacosApplication.class, args);
    }

}
