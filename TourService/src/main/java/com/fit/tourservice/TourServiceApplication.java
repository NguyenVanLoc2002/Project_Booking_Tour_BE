package com.fit.tourservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@EnableR2dbcRepositories(basePackages = "com.fit.tourservice.repositories.r2dbc")
@EnableRedisRepositories(basePackages = "com.fit.tourservice.repositories.redis")
@SpringBootApplication(scanBasePackages = {"com.fit.tourservice", "com.fit.commonservice"})
@EnableDiscoveryClient
public class TourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TourServiceApplication.class, args);
    }
}
