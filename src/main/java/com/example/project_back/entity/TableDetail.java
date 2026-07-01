package com.example.project_back.entity;

import com.example.project_back.constant.TableStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "table_details")
@Getter
@Setter
public class TableDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "table_number")
    private String tableNumber;

    private Integer capacity;

    @Column(name = "qr_code")
    private String qrCode;

    @Enumerated(EnumType.STRING)
    private TableStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}