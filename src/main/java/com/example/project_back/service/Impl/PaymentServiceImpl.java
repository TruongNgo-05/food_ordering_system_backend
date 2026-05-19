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
import java.text.SimpleDateFormat;
import java.util.*;

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

            // VNPAY bắt buộc *100
            long amount = (long) (order.getTotalPrice() * 100);

            Map<String, String> params = new HashMap<>();

            params.put("vnp_Version", "2.1.0");

            params.put("vnp_Command", "pay");

            params.put("vnp_TmnCode", vnPayConfig.getTmnCode());

            params.put("vnp_Amount", String.valueOf(amount));

            params.put("vnp_CurrCode", "VND");

            params.put("vnp_BankCode", "NCB");

            params.put("vnp_TxnRef", orderCode);

            params.put(
                    "vnp_OrderInfo",
                    "Thanh toan don hang " + orderCode
            );

            params.put("vnp_OrderType", "other");

            params.put("vnp_Locale", "vn");

            params.put(
                    "vnp_ReturnUrl",
                    vnPayConfig.getReturnUrl()
            );

            params.put("vnp_IpAddr", "14.225.206.161");

            Calendar cld =
                    Calendar.getInstance(
                            TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
                    );

            SimpleDateFormat formatter =
                    new SimpleDateFormat("yyyyMMddHHmmss");

            String createDate =
                    formatter.format(cld.getTime());

            params.put("vnp_CreateDate", createDate);

            cld.add(Calendar.MINUTE, 15);

            String expireDate =
                    formatter.format(cld.getTime());

            params.put("vnp_ExpireDate", expireDate);

            // QUERY URL
            String queryUrl =
                    VNPayUtil.buildQuery(
                            params,
                            true,
                            true
                    );

            // HASH DATA
            Map<String, String> signParams = new HashMap<>(params);
            signParams.remove("vnp_SecureHash");
            signParams.remove("vnp_SecureHashType");

            String hashData = VNPayUtil.buildQuery(signParams, false, false);

            String secureHash =
                    VNPayUtil.hmacSHA512(
                            vnPayConfig.getHashSecret(),
                            hashData
                    );

            System.out.println("===== CREATE PAYMENT =====");
            System.out.println("HASH DATA:");
            System.out.println(hashData);

            System.out.println("SECURE HASH:");
            System.out.println(secureHash);

            return vnPayConfig.getPayUrl()
                    + "?"
                    + queryUrl
                    + "&vnp_SecureHash="
                    + secureHash;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Cannot create payment url"
            );
        }
    }

    @Override
    @Transactional
    public void paymentCallback(HttpServletRequest request) {

        String orderCode =
                request.getParameter("vnp_TxnRef");

        String responseCode =
                request.getParameter("vnp_ResponseCode");

        String transactionNo =
                request.getParameter("vnp_TransactionNo");

        String secureHash =
                request.getParameter("vnp_SecureHash");

        // GET PARAMS
        Map<String, String> params = new HashMap<>();

        request.getParameterMap().forEach((key, values) -> {

            if (values.length > 0) {

                params.put(key, values[0]);
            }
        });

        // VERIFY HASH
        boolean hashValid =
                VNPayUtil.verify(
                        params,
                        vnPayConfig.getHashSecret(),
                        secureHash
                );

        if (!hashValid) {

            throw new RuntimeException(
                    "Invalid secure hash"
            );
        }

        Order order =
                orderRepository.findByOrderCode(orderCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        Payment payment =
                paymentRepository.findByOrderId(order.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found"
                                )
                        );

        // Đã thanh toán rồi
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        // SUCCESS
        if ("00".equals(responseCode)) {

            payment.setStatus(PaymentStatus.PAID);

            payment.setPaidAt(LocalDateTime.now());

            payment.setTransactionId(transactionNo);

            order.setStatus(OrderStatus.CONFIRMED);

        }

        // FAILED
        else {

            payment.setStatus(PaymentStatus.FAILED);

            order.setStatus(OrderStatus.CANCELED);
        }

        paymentRepository.save(payment);

        orderRepository.save(order);

        System.out.println("===== PAYMENT SUCCESS =====");
        System.out.println("ORDER: " + orderCode);
        System.out.println("RESPONSE CODE: " + responseCode);
    }
}