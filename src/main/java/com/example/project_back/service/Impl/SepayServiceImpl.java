package com.example.project_back.service.Impl;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.entity.Order;
import com.example.project_back.entity.Payment;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.repository.PaymentRepository;
import com.example.project_back.service.SepayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SepayServiceImpl implements SepayService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${sepay.bank-account}")
    private String bankAccount;

    @Value("${sepay.bank-name}")
    private String bankName;

    @Override
    public String generateQr(String orderCode, Double amount) {

        return "https://img.vietqr.io/image/"
                + bankName
                + "-"
                + bankAccount
                + "-compact2.png"
                + "?amount=" + amount.intValue()
                + "&addInfo=" + orderCode;
    }


    @Override
    public void confirmPayment(String orderCode) {

        Order order = orderRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() ->
                        new ApplicationException("Order không tồn tại"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() ->
                        new ApplicationException("Payment không tồn tại"));

        payment.setStatus(PaymentStatus.PAID);

        order.setStatus(OrderStatus.CONFIRMED);

        paymentRepository.save(payment);

        orderRepository.save(order);
    }
}
