package com.orchard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OrchardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchardServiceApplication.class, args);
    }

}
