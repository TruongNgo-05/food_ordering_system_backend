package com.example.project_back.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonPropertyOrder({"id","voucherCode","description","discount","maxDiscount","minOrderValue","usageLimit","usedCount","startDate","endDate","createdAt"})
public class VoucherAdminResponse {

    private Integer id;
    private String voucherCode;
    private String description;

    private Double discount;

    private Integer usageLimit;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
