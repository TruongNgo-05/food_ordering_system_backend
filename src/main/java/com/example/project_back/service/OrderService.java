package com.example.project_back.service;

import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.CreateOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;

import java.util.List;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
    List<MyOrderResponse> getMyOrders();
    OrderDetailResponse getOrderDetail(Integer orderId);
    void cancelOrder(Integer orderId);
    void reorder(Integer orderId);
}
