package com.example.project_back.mapper;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.request.customer.order.CreateOrderTableRequest;
import com.example.project_back.dto.request.customer.order.OrderTableItemRequest;
import com.example.project_back.dto.response.admin.OrderAdminResponse;
import com.example.project_back.dto.response.customer.order.*;
import com.example.project_back.dto.response.customer.order.reponseOrder.PaymentOrder;
import com.example.project_back.dto.response.customer.order.reponseOrder.VoucherOrder;
import com.example.project_back.dto.response.staff.OrderDetailStaffResponse;
import com.example.project_back.dto.response.staff.OrderItemStaffResponse;
import com.example.project_back.dto.response.staff.OrderStaffOffLineResponse;
import com.example.project_back.dto.response.staff.OrderStaffOnLineResponse;
import com.example.project_back.entity.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    // ONLINE ORDER
    public static Order toOrder(
            User user,
            UserAddress address,
            PaymentMethod paymentMethod,
            Voucher voucher,
            CreateOrderRequest request,
            Double total,
            Double discount
    ) {

        Order order = new Order();
        order.setOrderCode("ORD-OL-" + System.currentTimeMillis());

        order.setUser(user);

        order.setCustomerName(user.getUsername());

        order.setCustomerPhone(user.getPhone());

        order.setAddress(address);

        order.setPaymentMethod(paymentMethod);

        order.setStatus(OrderStatus.PENDING);

        order.setTotalPrice(total);

        order.setDiscount(discount);

        order.setVoucher(voucher);

        order.setCreatedAt(LocalDateTime.now());

        order.setUpdatedAt(LocalDateTime.now());

        order.setNote(request.getNote());

        return order;
    }

    // COD ORDER
    public static Order toCodOrder(
            User user,
            UserAddress address,
            PaymentMethod paymentMethod,
            Voucher voucher,
            CreateOrderRequest request,
            Double total,
            Double discount
    ) {

        Order order = new Order();
        order.setUser(user);

        order.setCustomerName(user.getUsername());

        order.setCustomerPhone(user.getPhone());

        order.setOrderCode("ORD-COD-" + System.currentTimeMillis());

        order.setAddress(address);

        order.setPaymentMethod(paymentMethod);

        order.setStatus(OrderStatus.PENDING);

        order.setTotalPrice(total);

        order.setDiscount(discount);

        order.setVoucher(voucher);

        order.setCreatedAt(LocalDateTime.now());

        order.setUpdatedAt(LocalDateTime.now());

        order.setNote(request.getNote());

        return order;
    }

    // PAYMENT
    public static Payment toPayment(
            Order order,
            PaymentMethod paymentMethod
    ) {

        Payment payment = new Payment();

        payment.setOrder(order);

        payment.setPaymentMethod(paymentMethod);

        payment.setStatus(PaymentStatus.PENDING);

        payment.setCreatedAt(LocalDateTime.now());

        return payment;
    }

    // ONLINE ORDER DETAILS
    public static List<OrderDetail> toOrderDetails(
            Order order,
            List<CartItem> cartItems
    ) {

        List<OrderDetail> details = new ArrayList<>();

        for (CartItem item : cartItems) {

            OrderDetail detail = new OrderDetail();

            detail.setOrder(order);

            detail.setFood(item.getFood());

            detail.setQuantity(item.getQuantity());

            detail.setPrice(item.getFood().getPrice());

            details.add(detail);
        }

        return details;
    }

    // TABLE ORDER

    public static Order toTableOrder(
            CreateOrderTableRequest request,
            TableDetail table,
            PaymentMethod paymentMethod,
            Double total
    ) {

        Order order = new Order();

        BeanUtils.copyProperties(request, order);

        order.setOrderCode("ORD-TB-" + System.currentTimeMillis());

        order.setStatus(OrderStatus.PENDING);

        order.setTotalPrice(total);

        order.setDiscount(0.0);

        order.setTable(table);

        order.setPaymentMethod(paymentMethod);

        order.setCreatedAt(LocalDateTime.now());

        order.setUpdatedAt(LocalDateTime.now());

        return order;
    }

    // TABLE ORDER DETAILS
    public static List<OrderDetail> toTableOrderDetails(
            Order order,
            List<Food> foods,
            List<OrderTableItemRequest> items
    ) {

        List<OrderDetail> details = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {

            Food food = foods.get(i);

            OrderTableItemRequest item = items.get(i);

            OrderDetail detail = new OrderDetail();

            detail.setOrder(order);

            detail.setFood(food);

            detail.setQuantity(item.getQuantity());

            detail.setPrice(food.getPrice());

            details.add(detail);
        }

        return details;
    }

    // CREATE ORDER RESPONSE
    public static OrderResponse toOrderResponse(
            Order order,
            String paymentUrl
    ) {

        OrderResponse response = new OrderResponse();

        BeanUtils.copyProperties(order, response);

        response.setOrderId(order.getId());

        response.setTotalPrice(order.getTotalPrice() - order.getDiscount());

        response.setPriceBefore(order.getTotalPrice());


        response.setPaymentUrl(paymentUrl);

        return response;
    }

    // TABLE ORDER RESPONSE
    public static OrderTableResponse toOrderTableResponse(
            Order order,
            String paymentUrl
    ) {

        OrderTableResponse response = new OrderTableResponse();

        response.setOrderId(order.getId());

        response.setOrderCode(order.getOrderCode());

        response.setCustomerName(order.getCustomerName());

        response.setCustomerPhone(order.getCustomerPhone());

        response.setTotalPrice(order.getTotalPrice());

        response.setStatus(order.getStatus());


        response.setPaymentUrl(paymentUrl);

        if (order.getTable() != null) {
            response.setTableNumber(order.getTable().getTableNumber());
        }

        response.setItems(toOrderItemResponses(order.getOrderDetails()));

        return response;
    }

    // ORDER ITEM RESPONSE
    public static List<OrderItemResponse> toOrderItemResponses(
            List<OrderDetail> details
    ) {

        List<OrderItemResponse> responses = new ArrayList<>();

        for (OrderDetail detail : details) {

            OrderItemResponse item = new OrderItemResponse();

            item.setFoodId(detail.getFood().getId());

            item.setFoodName(detail.getFood().getName());

            item.setImage(detail.getFood().getImage());

            item.setPrice(detail.getPrice());

            item.setQuantity(detail.getQuantity());

            responses.add(item);
        }

        return responses;
    }

    // MY ORDER RESPONSE
    public static MyOrderResponse toMyOrderResponse(
            Order order,
            Payment payment
    ) {

        MyOrderResponse response = new MyOrderResponse();

        BeanUtils.copyProperties(order, response);

        response.setOrderId(order.getId());

        Double discount = order.getDiscount() == null ? 0.0 : order.getDiscount();

        response.setTotalPrice(order.getTotalPrice() - discount);

        // PAYMENT
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

        response.setOrder(toMyOrderResponse(order, payment));

        // VOUCHER
        if (order.getVoucher() != null) {

            VoucherOrder voucherOrder = new VoucherOrder();

            voucherOrder.setVoucherCode(order.getVoucher().getCode());

            voucherOrder.setDiscount(order.getDiscount());

            response.setVoucherOrder(voucherOrder);
        }

        response.setPriceBefore(order.getTotalPrice());

        if (order.getAddress() != null) {

            response.setAddress(order.getAddress().getAddress());
        }

        response.setNote(order.getNote());

        return response;
    }

// ================= STAFF ONLINE =================

    public static OrderStaffOnLineResponse toOnlineResponse(
            Order order,
            Payment payment
    ) {

        OrderStaffOnLineResponse response = new OrderStaffOnLineResponse();

       BeanUtils.copyProperties(order, response);

       response.setOrderId(order.getId());

       response.setCustomerName(order.getCustomerName());

        if (order.getPaymentMethod() != null) {
            response.setPaymentMethod(order.getPaymentMethod().getCode().name());
        }

        if (payment != null) {
            response.setPaymentStatus(payment.getStatus().name());
        }

        return response;
    }

    // ================= STAFF OFFLINE =================

    public static OrderStaffOffLineResponse toOfflineResponse(
            Order order,
            Payment payment
    ) {

        OrderStaffOffLineResponse response = new OrderStaffOffLineResponse();

        response.setOrderId(order.getId());

      BeanUtils.copyProperties(order, response);

        if (order.getTable() != null) {
            response.setTableNumber(order.getTable().getTableNumber());
        }

        if (order.getPaymentMethod() != null) {
            response.setPaymentMethod(order.getPaymentMethod().getCode().name());
        }

        if (payment != null) {
            response.setPaymentStatus(payment.getStatus().name());
        }

        return response;
    }

    // ================= STAFF DETAIL =================

    public static OrderDetailStaffResponse toStaffDetailResponse(
            Order order,
            Payment payment
    ) {

        OrderDetailStaffResponse response = new OrderDetailStaffResponse();

       BeanUtils.copyProperties(order, response);

       response.setOrderId(order.getId());

        if (order.getAddress() != null) {
            response.setAddress(order.getAddress().getAddress());
        }

        if (order.getTable() != null) {
            response.setTableNumber(order.getTable().getTableNumber());
        }

        if (order.getPaymentMethod() != null) {
            response.setPaymentMethod(order.getPaymentMethod().getCode().name()
            );
        }

        if (payment != null) {
            response.setPaymentStatus(payment.getStatus().name());
        }

        List<OrderItemStaffResponse> items =
                new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {

            OrderItemStaffResponse item = new OrderItemStaffResponse();

            item.setFoodId(detail.getFood().getId().longValue());

            item.setFoodName(detail.getFood().getName());

            item.setImage(detail.getFood().getImage());

            item.setPrice(detail.getPrice());

            item.setQuantity(detail.getQuantity());

            items.add(item);
        }

        response.setItems(items);

        return response;
    }


    public static OrderAdminResponse toAdminResponse(
            Order order,
            Payment payment
    ){
        OrderAdminResponse response = new OrderAdminResponse();
        BeanUtils.copyProperties(order, response);
        response.setOrderId(order.getId());
        if (order.getPaymentMethod() != null) {
            response.setPaymentMethod(order.getPaymentMethod().getCode().name());
        }

        if (payment != null) {
            response.setPaymentStatus(payment.getStatus().name());
        }
        return response;
    }
}