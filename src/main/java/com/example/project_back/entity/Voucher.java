package com.example.project_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private Double discount;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name="usage_count")
    private Integer usedCount = 0;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
