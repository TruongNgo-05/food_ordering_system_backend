package com.example.project_back.mapper;

import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
import com.example.project_back.dto.response.customer.order.OrderItemResponse;
import com.example.project_back.dto.response.customer.order.OrderResponse;
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
            Double totalAfter,
            String paymentUrl
    ) {

        OrderResponse response = new OrderResponse();

        BeanUtils.copyProperties(order, response);

        response.setStatus(order.getStatus().name());

        response.setTotalAfter(totalAfter);

        response.setPaymentUrl(paymentUrl);

        return response;
    }

    public static MyOrderResponse toMyOrderResponse(Order order) {

        MyOrderResponse response = new MyOrderResponse();

        response.setOrderId(order.getId());

        response.setOrderCode(order.getOrderCode());

        response.setStatus(order.getStatus().name());

        // GIÁ CUỐI CÙNG
        response.setTotalPrice(
                order.getTotalPrice() - order.getDiscount()
        );

        response.setCreatedAt(order.getCreatedAt());

        response.setPaymentMethod(
                order.getPaymentMethod().getCode().name()
        );

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

        BeanUtils.copyProperties(order, response);

        response.setStatus(order.getStatus().name());

        response.setPaymentMethod(
                order.getPaymentMethod().getCode().name()
        );

        response.setTotalAfter(
                order.getTotalPrice() - order.getDiscount()
        );

        // payment status
        if (payment != null) {
            response.setPaymentStatus(
                    payment.getStatus().name()
            );
        }

        // address
        if (order.getAddress() != null) {
            response.setAddress(
                    order.getAddress().getAddress()
            );
        }

        // items
        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {

            OrderItemResponse item = new OrderItemResponse();

            item.setFoodId(detail.getFood().getId());

            item.setFoodName(detail.getFood().getName());

            item.setImage(detail.getFood().getImage());

            item.setPrice(detail.getPrice());

            item.setQuantity(detail.getQuantity());

            items.add(item);
        }

        response.setItems(items);

        return response;
    }
}