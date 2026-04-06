package com.example.project_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync// tăng tốc độ gửi mail
public class ProjectBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBackApplication.class, args);
    }

}
