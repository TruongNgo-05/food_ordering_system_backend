package com.example.project_back.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Table(name = "faqs")
@Data
public class Faqs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String category;

    @Column(length = 500)
    private String question;

    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    private Integer orderBy ;
    private Boolean isActive ;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
