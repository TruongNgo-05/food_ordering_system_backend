package com.example.project_back.service.Impl;

import com.example.project_back.config.VNPayConfig;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.entity.Order;
import com.example.project_back.entity.Payment;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.repository.PaymentRepository;
import com.example.project_back.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
//
//    // ================= CREATE PAYMENT URL =================
//    @Override
//    public String createVNPayUrl(Long orderId) {
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseGet(() -> {
//                    Payment p = new Payment();
//                    p.setOrder(order);
//                    p.setStatus(PaymentStatus.PENDING);
//                    return paymentRepository.save(p);
//                });
//
//        Map<String, String> params = new HashMap<>();
//
//        params.put("vnp_Version", "2.1.0");
//        params.put("vnp_Command", "pay");
//        params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
//        params.put("vnp_Amount", String.valueOf((long) (order.getTotalPrice() * 100)));
//        params.put("vnp_CurrCode", "VND");
//
//        params.put("vnp_TxnRef", String.valueOf(order.getId()));
//        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
//        params.put("vnp_OrderType", "other");
//
//        params.put("vnp_Locale", "vn");
//        params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
//        params.put("vnp_IpAddr", "127.0.0.1");
//
//        params.put("vnp_CreateDate",
//                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//
//        //  encode TRUE khi tạo URL
//        String query = VNPayUtil.buildQuery(params, true);
//
//        String secureHash = VNPayUtil.hmacSHA512(
//                vnpayConfig.getHashSecret(),
//                VNPayUtil.buildQuery(params, false)
//        );
//
//        return vnpayConfig.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
//    }
//
//    // ================= RETURN CALLBACK =================
//    @Override
//    @Transactional
//    public String handleVNPayReturn(HttpServletRequest request) {
//
//        Map<String, String> fields = new HashMap<>();
//        request.getParameterMap().forEach((k, v) -> fields.put(k, v[0]));
//
//        String secureHash = fields.get("vnp_SecureHash");
//
//        boolean valid = VNPayUtil.verify(fields, vnpayConfig.getHashSecret(), secureHash);
//
//        if (!valid) {
//            return "Invalid signature";
//        }
//
//        String responseCode = fields.get("vnp_ResponseCode");
//        Long orderId = Long.parseLong(fields.get("vnp_TxnRef"));
//
//        Payment payment = paymentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new RuntimeException("Payment not found"));
//
//        Order order = payment.getOrder();
//
//        if (payment.getStatus() == PaymentStatus.PAID) {
//            return "Already paid";
//        }
//
//        if (order.getStatus() != OrderStatus.PENDING) {
//            return "Order already processed";
//        }
//
//        if ("00".equals(responseCode)) {
//
//            payment.setStatus(PaymentStatus.PAID);
//            payment.setPaidAt(LocalDateTime.now());
//
//            order.setStatus(OrderStatus.CONFIRMED);
//
//        } else {
//            payment.setStatus(PaymentStatus.FAILED);
//            order.setStatus(OrderStatus.CANCELED);
//        }
//
//        paymentRepository.save(payment);
//        orderRepository.save(order);
//
//        return "Payment: " + payment.getStatus();
//    }

    @Override
    public String createPaymentUrl(Order order) {
        try {
            String orderCode = order.getOrderCode();
            long amount = (long) (order.getTotalPrice() * 100);
            String returnUrl = URLEncoder.encode("http://localhost:8080/api/payments/vnpay-callback", StandardCharsets.UTF_8);
            String paymentUrl = vnPayConfig.getPayUrl() + "?vnp_Version=2.1.0" + "&vnp_Command=pay" + "&vnp_TmnCode=" + vnPayConfig.getTmnCode() + "&vnp_Amount=" + amount + "&vnp_CurrCode=VND" + "&vnp_TxnRef=" + orderCode + "&vnp_OrderInfo=Thanh toan don hang " + orderCode + "&vnp_OrderType=food" + "&vnp_Locale=vn" + "&vnp_ReturnUrl=" + returnUrl + "&vnp_IpAddr=127.0.0.1";
            return paymentUrl;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create payment url");
        }
    }

    @Override
    public void paymentCallback(HttpServletRequest request) {
        String orderCode = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Payment payment = paymentRepository.findByOrderId(Long.valueOf(order.getId()))
                .orElseThrow(() -> new RuntimeException("Payment not found")); // SUCCESS
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(transactionNo);
            order.setStatus(OrderStatus.CONFIRMED); } // FAILED
         else {
             payment.setStatus(PaymentStatus.FAILED);
             order.setStatus(OrderStatus.CANCELED);
         }
         paymentRepository.save(payment);
         orderRepository.save(order); }
    }