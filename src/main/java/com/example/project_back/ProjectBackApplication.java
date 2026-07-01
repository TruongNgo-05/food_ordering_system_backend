package com.example.project_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync// tăng tốc độ gửi mail
@EnableScheduling // tu dong chay check tgian dat ban
public class
ProjectBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBackApplication.class, args);
    }

}
