package com.example.project_back.mapper;

import com.example.project_back.dto.response.customer.order.*;
import com.example.project_back.dto.response.customer.order.reponseOrder.PaymentOrder;
import com.example.project_back.dto.response.customer.order.reponseOrder.VoucherOrder;
import com.example.project_back.entity.Order;
import com.example.project_back.entity.OrderDetail;
import com.example.project_back.entity.Payment;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    // CREATE ORDER RESPONSE
    public static OrderResponse toOrderResponse(
            Order order,
            String paymentUrl
    ) {

        OrderResponse response = new OrderResponse();

        BeanUtils.copyProperties(order, response);

        response.setOrderId(order.getId());

        response.setStatus(order.getStatus());

        response.setTotalPrice(order.getTotalPrice()-order.getDiscount());

        response.setPriceBefore(order.getTotalPrice());

        response.setPaymentUrl(paymentUrl);

        return response;
    }

    public static MyOrderResponse toMyOrderResponse(Order order,Payment  payment) {

        MyOrderResponse response = new MyOrderResponse();

        response.setOrderId(order.getId());

        response.setOrderCode(order.getOrderCode());

        response.setStatus(order.getStatus());

        // GIÁ CUỐI CÙNG
        response.setTotalPrice(order.getTotalPrice() - order.getDiscount());

        response.setCreatedAt(order.getCreatedAt());

        // payment
        if (payment != null) {

            PaymentOrder paymentOrder = new PaymentOrder();

            paymentOrder.setPaymentStatus(payment.getStatus());

            paymentOrder.setPaymentMethodType(payment.getPaymentMethod().getCode());

            response.setPayment(paymentOrder);
        }

        int totalItems = 0;

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {

            OrderItemResponse item = new OrderItemResponse();

            item.setFoodId(detail.getFood().getId());

            item.setFoodName(detail.getFood().getName());

            item.setImage(detail.getFood().getImage());

            item.setPrice(detail.getPrice());

            item.setQuantity(detail.getQuantity());

            itemResponses.add(item);

            totalItems += detail.getQuantity();
        }

        response.setItems(itemResponses);

        response.setTotalItems(totalItems);

        return response;
    }


    // ORDER DETAIL RESPONSE
    public static OrderDetailResponse toOrderDetailResponse(
            Order order,
            Payment payment
    ) {

        OrderDetailResponse response = new OrderDetailResponse();

        response.setOrder(toMyOrderResponse(order,payment ));
        // voucher
        if (order.getVoucher() != null) {

            VoucherOrder voucherOrder = new VoucherOrder();

            voucherOrder.setVoucherCode(order.getVoucher().getCode());

            voucherOrder.setDiscount(order.getDiscount());

            response.setVoucherOrder(voucherOrder);
        }
        response.setPriceBefore(order.getTotalPrice());

        // address
        if (order.getAddress() != null) {
            response.setAddress(order.getAddress().getAddress());
        }
        //note
        response.setNote(order.getNote());

        return response;
    }
}