package com.fptgamebookingbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FptGameBookingBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FptGameBookingBeApplication.class, args);
    }

}
