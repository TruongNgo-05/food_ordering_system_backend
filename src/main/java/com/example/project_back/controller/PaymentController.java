package com.example.project_back.controller;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.entity.Order;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class  PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

@GetMapping("/vnpay-return")
public ResponseEntity<?> vnpayReturn(HttpServletRequest request) {

    try {

        paymentService.paymentCallback(request);

        return ResponseEntity.ok(BaseResponse.success("Payment success"));

    } catch (Exception e) {

        return ResponseEntity.badRequest().body(BaseResponse.error(e.getMessage()));
    }
}

//    @GetMapping("/test-payment")
//    public ResponseEntity<?> testPayment() {
//
//        Order order = orderRepository.findById(3).orElseThrow();
//
//        String paymentUrl = paymentService.createPaymentUrl(order);
//
//        return ResponseEntity.ok(BaseResponse.success(paymentUrl));
//    }

}