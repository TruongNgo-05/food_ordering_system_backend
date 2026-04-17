package com.example.project_back.entity;

import com.example.project_back.constant.VoucherType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    private VoucherType type;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
