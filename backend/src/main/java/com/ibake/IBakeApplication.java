package com.ibake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IBakeApplication {

    public static void main(String[] args) {
        SpringApplication.run(IBakeApplication.class, args);
    }
}
