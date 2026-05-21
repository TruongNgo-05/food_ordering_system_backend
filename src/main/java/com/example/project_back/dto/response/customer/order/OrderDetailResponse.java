package com.example.project_back.dto.response.customer.order;

import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.dto.response.customer.order.reponseOrder.VoucherOrder;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"order","voucherOrder","address","note"})
public class OrderDetailResponse {
    private MyOrderResponse order;
    private VoucherOrder voucherOrder;
    private Double priceBefore;
    private String note;
    private String address;
}
