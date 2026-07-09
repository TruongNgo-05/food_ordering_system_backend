package com.example.project_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String description;

    private Double discount;
    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "usage_limit")
    private Integer usageLimit;
    @Column(name="used_count")
    private Integer usedCount ;

    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "created_at")
    @CreationTimestamp()
    private LocalDateTime createdAt;
}