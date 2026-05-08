package com.example.project_back.service;

import com.example.project_back.entity.Order;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {

//  vnpay
//    String createVNPayUrl(Long orderId);
//    String handleVNPayReturn(HttpServletRequest request);
String createPaymentUrl(Order order);
void paymentCallback(HttpServletRequest request);
}
