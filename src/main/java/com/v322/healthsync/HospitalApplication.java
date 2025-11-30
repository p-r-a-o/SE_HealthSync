package com.v322.healthsync;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.v322.healthsync.repository")
@EntityScan(basePackages = "com.v322.healthsync.entity")
@EnableAutoConfiguration(exclude = { 
    SecurityAutoConfiguration.class 
})
public class HospitalApplication {

	public static void main(String[] args) {
		
		SpringApplicationBuilder app = new SpringApplicationBuilder(HospitalApplication.class);
        app.run(args);
	}

}
