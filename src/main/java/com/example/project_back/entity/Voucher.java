package com.example.project_back.entity;

import com.example.project_back.constant.VoucherType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String code;

    private Double discount;

    @Enumerated(EnumType.STRING)
    private VoucherType type;

    @Column(name = "expired_at")
    private LocalDate expiredAt;
}
