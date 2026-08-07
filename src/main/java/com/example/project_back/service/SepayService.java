package com.example.project_back.service;

import com.example.project_back.dto.response.PaymentStatusResponse;

public interface SepayService {

    String generateQr(String orderCode, Double amount);
    void confirmPayment(String orderCode,String transactionId);

    PaymentStatusResponse getPaymentStatus(String orderCode);
}
