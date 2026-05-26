package com.example.project_back.service;

import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.request.customer.order.CreateOrderTableRequest;
import com.example.project_back.dto.request.spec.OrderRequestParam;
import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
import com.example.project_back.dto.response.customer.order.OrderTableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    //customer
    OrderResponse createOrder(CreateOrderRequest request);
    Page<MyOrderResponse> getMyOrders(OrderRequestParam  param, Pageable pageable);
    OrderDetailResponse getOrderDetail(Long orderId);
    void cancelOrder(Long orderId);
    void reorder(Long orderId);

    OrderTableResponse createOrderTb(CreateOrderTableRequest request);

    // admin
}
