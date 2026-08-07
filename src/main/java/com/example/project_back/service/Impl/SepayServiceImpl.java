package com.example.project_back.service.Impl;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.constant.TableStatus;
import com.example.project_back.dto.response.PaymentStatusResponse;
import com.example.project_back.entity.*;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.CartRepository;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.repository.PaymentRepository;
import com.example.project_back.repository.VoucherRepository;
import com.example.project_back.service.SepayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SepayServiceImpl implements SepayService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final VoucherRepository voucherRepository;

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
    @Transactional
    public void confirmPayment(String orderCode, String transactionId) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ApplicationException("Order không tồn tại"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ApplicationException("Payment không tồn tại"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        // ================= PAYMENT =================
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId(transactionId);
        payment.setUpdatedAt(LocalDateTime.now());

        // ================= ORDER =================
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        // ================= VOUCHER =================
        Voucher voucher = order.getVoucher();

        if (voucher != null) {

            int used = voucher.getUsedCount() == null
                    ? 0
                    : voucher.getUsedCount();

            voucher.setUsedCount(used + 1);

            voucherRepository.save(voucher);
        }

        // ================= CLEAR CART (chỉ ONLINE) =================
        if (order.getUser() != null && order.getTable() == null) {

            Cart cart = cartRepository.findByUser_Id(order.getUser().getId())
                    .orElse(null);

            if (cart != null) {

                cart.getItems().clear();

                cartRepository.save(cart);
            }
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(String orderCode) {

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ApplicationException("Order không tồn tại"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ApplicationException("Payment không tồn tại"));

        PaymentStatusResponse response = new PaymentStatusResponse();

        response.setOrderCode(order.getOrderCode());
        response.setPaymentStatus(payment.getStatus());
        response.setOrderStatus(order.getStatus());

        return response;
    }
}
