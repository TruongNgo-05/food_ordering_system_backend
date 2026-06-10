package com.example.project_back.service;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.request.customer.order.CreateOrderTableRequest;
import com.example.project_back.dto.request.spec.OrderRequestParam;
import com.example.project_back.dto.response.admin.OrderAdminResponse;
import com.example.project_back.dto.response.staff.OrderStaffOffLineResponse;
import com.example.project_back.dto.response.staff.OrderStaffOnLineResponse;
import com.example.project_back.dto.response.staff.OrderDetailStaffResponse;
import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
import com.example.project_back.dto.response.customer.order.OrderTableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    //customer
    OrderResponse createOrder(CreateOrderRequest request);
    Page<MyOrderResponse> getMyOrders(OrderRequestParam  param, Pageable pageable);
    OrderDetailResponse getOrderDetail(Long orderId);
    void cancelOrder(Long orderId);
    void reorder(Long orderId);

    OrderTableResponse createOrderTb(CreateOrderTableRequest request);

    // staff
    Page<OrderStaffOnLineResponse> getOnlineOrders(OrderRequestParam param , Pageable pageable);
    Page<OrderStaffOffLineResponse> getTableOrders(OrderRequestParam param , Pageable pageable);

    OrderDetailStaffResponse getOrderStaffDetail(Long orderId);

    void updateStatus(Long orderId, OrderStatus status);

//    admin

Page<OrderAdminResponse> getAllAdminOrders(
        OrderRequestParam param,
        Pageable pageable
) ;
}
