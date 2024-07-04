package com.green.greengram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GreengramSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreengramSecurityApplication.class, args);
    }

}
