package com.example.project_back.entity;

import com.example.project_back.constant.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="table_reservations")
public class TableReservations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="reservation_code" ,unique = true)
    private String reservationCode;

    @Column(name="customer_name")
    private String customerName;

    @Column(name="customer_phone")
    private String customerPhone;

    @Column(name="customer_email")
    private String customerEmail;

    @Column(name="reservation_time")
    private LocalDateTime reservationTime;

    private String note;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableDetail table;
}
