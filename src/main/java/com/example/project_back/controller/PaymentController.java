package com.example.project_back.controller;

import com.example.project_back.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    //  Tạo link VNPay
//    @GetMapping("/vnpay/create")
//    public String createVNPay(@RequestParam Long orderId) {
//        return paymentService.createVNPayUrl(orderId);
//    }
//
//    // Callback VNPay
//    @GetMapping("/vnpay-return")
//    public String vnpayReturn(HttpServletRequest request) {
//        return paymentService.handleVNPayReturn(request);
//    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<?> paymentCallback( HttpServletRequest request ) {
        paymentService.paymentCallback(request);
        return ResponseEntity.ok("Payment success");
    }
}