package com.example.project_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name="created_at",nullable=false,updatable=false)
    @CreationTimestamp()
    private LocalDateTime createdAt;

    @Column(name="updated_at",nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}