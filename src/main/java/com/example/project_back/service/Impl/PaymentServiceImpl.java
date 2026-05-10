package com.example.project_back.service.Impl;

import com.example.project_back.config.VNPayConfig;
import com.example.project_back.config.VNPayUtil;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.entity.Order;
import com.example.project_back.entity.Payment;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.repository.PaymentRepository;
import com.example.project_back.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public String createPaymentUrl(Order order) {

        try {

            String orderCode = order.getOrderCode();

            long amount = (long) (order.getTotalPrice() * 100);

            Map<String, String> params = new HashMap<>();

            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", vnPayConfig.getTmnCode());

            params.put("vnp_Amount", String.valueOf(amount));

            params.put("vnp_CurrCode", "VND");

            params.put("vnp_TxnRef", orderCode);

            params.put("vnp_OrderInfo", "Thanh toan don hang " + orderCode);

            params.put("vnp_OrderType", "food");

            params.put("vnp_Locale", "vn");

            params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());

            params.put("vnp_IpAddr", "127.0.0.1");

            params.put("vnp_SecureHashType", "HmacSHA512");

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

            java.text.SimpleDateFormat formatter =
                    new java.text.SimpleDateFormat("yyyyMMddHHmmss");

            String createDate = formatter.format(cld.getTime());

            params.put("vnp_CreateDate", createDate);

            cld.add(Calendar.MINUTE, 15);

            String expireDate = formatter.format(cld.getTime());

            params.put("vnp_ExpireDate", expireDate);

            String queryUrl =
                    VNPayUtil.buildQuery(params, false, true);

            String hashData =
                    VNPayUtil.buildQuery(params, false, true);

            String secureHash =
                    VNPayUtil.hmacSHA512(
                            vnPayConfig.getHashSecret(),
                            hashData
                    );

            return vnPayConfig.getPayUrl()
                    + "?"
                    + queryUrl
                    + "&vnp_SecureHash="
                    + secureHash;

        } catch (Exception e) {
            throw new RuntimeException("Cannot create payment url", e);
        }
    }

    @Override
    @Transactional
    public void paymentCallback(HttpServletRequest request) {
        String orderCode = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String secureHash = request.getParameter("vnp_SecureHash");

        // Validate secure hash
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (!key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                params.put(key, values[0]);
            }
        });

        boolean hashValid = VNPayUtil.verify(params, vnPayConfig.getHashSecret(), secureHash);
        if (!hashValid) {
            throw new RuntimeException("Invalid secure hash");
        }

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderCode));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));

        // Check if payment already processed
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order status is not pending");
        }

        // Process payment response
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(transactionNo);
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELED);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}