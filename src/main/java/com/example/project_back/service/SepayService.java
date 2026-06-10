package com.example.project_back.service;

public interface SepayService {

    String generateQr(String orderCode, Double amount);
    void confirmPayment(String orderCode,String transactionId);
}
