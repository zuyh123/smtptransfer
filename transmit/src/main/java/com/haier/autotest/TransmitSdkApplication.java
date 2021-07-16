package com.haier.autotest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.haier.autotest"})
@EnableScheduling
public class TransmitSdkApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransmitSdkApplication.class, args);
    }
}
