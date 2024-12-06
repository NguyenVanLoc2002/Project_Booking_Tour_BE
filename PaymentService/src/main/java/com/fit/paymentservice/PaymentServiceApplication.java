package com.fit.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.fit.paymentservice", "com.fit.commonservice"})
@EnableR2dbcRepositories
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

}
