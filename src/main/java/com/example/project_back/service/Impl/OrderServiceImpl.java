package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentMethodType;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.response.customer.order.CreateOrderResponse;
import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
import com.example.project_back.dto.response.customer.order.OrderItemResponse;
import com.example.project_back.entity.*;
import com.example.project_back.repository.*;
import com.example.project_back.service.OrderService;
import com.example.project_back.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final VoucherRepository voucherRepository;
    private final UserAddressRepository userAddressRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;

        Order order = new Order();
        order.setOrderCode("ORD-" + System.currentTimeMillis());
        order.setUser(user);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());

        // Set address if provided
        if (request.getAddressId() != null) {
            UserAddress address = userAddressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            order.setAddress(address);
        }

        // Set table if provided (for dine-in orders)
        if (request.getTableId() != null) {
            TableDetail table = new TableDetail();
            table.setId(request.getTableId());
            order.setTable(table);
        }

        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        order.setPaymentMethod(paymentMethod);

        double discount = 0;

        if (request.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Voucher not found"));
            discount = voucher.getDiscount();
            order.setVoucher(voucher);
        }

        order = orderRepository.save(order);

        // Create order details and calculate total
        for (CartItem item : cart.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setFood(item.getFood());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getFood().getPrice());
            orderDetailRepository.save(detail);

            total += item.getFood().getPrice() * item.getQuantity();
        }

        total -= discount;
        order.setDiscount(discount);
        order.setTotalPrice(total);
        orderRepository.save(order);

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        // Clear cart
        cartItemRepository.deleteAll(cart.getItems());

        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setTotalPrice(total);
        response.setStatus(order.getStatus().name());

        // Generate payment URL if payment method is online
        if (PaymentMethodType.ONLINE.equals(paymentMethod.getCode())) {
            String paymentUrl = paymentService.createPaymentUrl(order);
            response.setPaymentUrl(paymentUrl);
        }

        return response;
    }

    @Override
    public List<MyOrderResponse> getMyOrders() {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                );

        List<MyOrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {

            MyOrderResponse response = new MyOrderResponse();

            response.setOrderId(order.getId());

            response.setOrderCode(order.getOrderCode());

            response.setStatus(order.getStatus().name());

            response.setTotalPrice(order.getTotalPrice());

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

                item.setTotalPrice(
                        detail.getPrice() * detail.getQuantity()
                );

                itemResponses.add(item);

                totalItems += detail.getQuantity();
            }

            response.setItems(itemResponses);

            response.setTotalItems(totalItems);

            responses.add(response);
        }

        return responses;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailResponse response = new OrderDetailResponse();

        response.setOrderId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setStatus(order.getStatus().name());
        response.setTotalPrice(order.getTotalPrice());
        response.setDiscount(order.getDiscount());
        response.setCreatedAt(order.getCreatedAt());
        response.setNote(order.getNote());
        response.setPaymentMethod(order.getPaymentMethod().getCode().name());

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);

        if (payment != null) {
            response.setPaymentStatus(payment.getStatus().name());
        }

        if (order.getAddress() != null) {
            response.setAddress(order.getAddress().getAddress());
        }

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {
            OrderItemResponse item = new OrderItemResponse();
            item.setFoodId(detail.getFood().getId());
            item.setFoodName(detail.getFood().getName());
            item.setImage(detail.getFood().getImage());
            item.setPrice(detail.getPrice());
            item.setQuantity(detail.getQuantity());
            item.setTotalPrice(detail.getPrice() * detail.getQuantity());
            itemResponses.add(item);
        }

        response.setItems(itemResponses);

        return response;
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.CANCELED);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void reorder(Integer orderId) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        for (OrderDetail detail : order.getOrderDetails()) {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setFood(detail.getFood());
            item.setQuantity(detail.getQuantity());
            cartItemRepository.save(item);
        }
    }
}

