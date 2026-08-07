package com.example.project_back.dto.request.sepay;

import lombok.Data;

@Data
public class SepayWebhookRequest {

    // Nội dung chuyển khoản
    private String content;

    // Mã giao dịch ngân hàng
    private String referenceCode;

    // Số tiền
    private Double transferAmount;

    // Ngân hàng
    private String gateway;
}