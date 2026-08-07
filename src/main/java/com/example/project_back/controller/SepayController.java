package com.example.project_back.controller;

import com.example.project_back.dto.request.sepay.SepayWebhookRequest;
import com.example.project_back.dto.response.PaymentStatusResponse;
import com.example.project_back.service.OrderService;
import com.example.project_back.service.SepayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sepay")
@RequiredArgsConstructor
public class SepayController {

    private final SepayService sepayService;
    private final OrderService orderService;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody SepayWebhookRequest request) {

        sepayService.confirmPayment(
                request.getContent(),
                request.getReferenceCode()
        );

        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/status/{orderCode}")
    public ResponseEntity<PaymentStatusResponse> getStatus(
            @PathVariable String orderCode
    ) {
        return ResponseEntity.ok(
                sepayService.getPaymentStatus(orderCode)
        );
    }

    @DeleteMapping("/pending/{orderCode}")
    public ResponseEntity<String> deletePendingOrder(
            @PathVariable String orderCode
    ) {

        orderService.deletePendingOrder(orderCode);

        return ResponseEntity.ok("Đã hủy đơn hàng");
    }

}