package com.demo.cc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for the Chore Service POC.
 * This application provides a REST API for managing chores in a family calendar.
 */
@SpringBootApplication
public class ChoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChoreServiceApplication.class, args);
    }
}
