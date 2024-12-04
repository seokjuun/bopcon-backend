package com.bopcon.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching  // 캐시 활성화
@EnableJpaAuditing // Auditing 활성화
public class BopconBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BopconBackendApplication.class, args);
    }
}
