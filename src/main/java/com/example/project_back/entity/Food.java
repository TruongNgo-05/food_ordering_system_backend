package com.example.project_back.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "foods")
@Getter
@Setter
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    private Double price;
    private String image;

    private Double rating;
    private Integer soldCount;
    private Boolean status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories categories;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<FoodImage> images;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<Review> reviews;
}