package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.response.customer.order.OrderResponse;
import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
import com.example.project_back.dto.response.customer.order.OrderItemResponse;
import com.example.project_back.entity.*;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.*;
import com.example.project_back.service.OrderService;
import com.example.project_back.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public OrderResponse createOrder(CreateOrderRequest request) {

    String username = SecurityUtils.getCurrentUsername();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ApplicationException("User không tồn tại"));

    Cart cart = cartRepository.findByUser_Id(user.getId())
            .orElseThrow(() ->
                    new ApplicationException("Cart không tồn tại"));

    if (cart.getItems().isEmpty()) {
        throw new ApplicationException("Cart trống");
    }

    UserAddress address = userAddressRepository
            .findById(request.getAddressId())
            .orElseThrow(() ->
                    new ApplicationException("Địa chỉ không tồn tại"));

    PaymentMethod paymentMethod = paymentMethodRepository
            .findById(request.getPaymentMethodId())
            .orElseThrow(() ->
                    new ApplicationException("Payment method không tồn tại"));

    Double total = 0.0;

    for (CartItem item : cart.getItems()) {

        Double itemTotal = item.getFood().getPrice() * item.getQuantity();

        total += itemTotal;
    }

    Order order = new Order();

    order.setOrderCode("ORD-" + System.currentTimeMillis());

    order.setUser(user);

    order.setAddress(address);

    order.setPaymentMethod(paymentMethod);

    order.setStatus(OrderStatus.PENDING);

    order.setDiscount(0.0);

    order.setTotalPrice(total);

    order.setCreatedAt(LocalDateTime.now());

    order.setUpdatedAt(LocalDateTime.now());

    order.setNote(request.getNote());

    order = orderRepository.save(order);

    ArrayList<OrderDetail> details = new ArrayList<>();

    for (CartItem item : cart.getItems()) {

        OrderDetail detail = new OrderDetail();

        detail.setOrder(order);

        detail.setFood(item.getFood());

        detail.setQuantity(item.getQuantity());

        detail.setPrice(item.getFood().getPrice());

        details.add(detail);
    }

    order.setOrderDetails(details);

    orderRepository.save(order);

    Payment payment = new Payment();

    payment.setOrder(order);

    payment.setPaymentMethod(paymentMethod);

    payment.setCreatedAt(LocalDateTime.now());

    payment.setStatus(PaymentStatus.PENDING);

    paymentRepository.save(payment);

    String paymentUrl = null;

    // COD
    if (paymentMethod.getCode().name().equals("COD")) {

        cart.getItems().clear();

        cartRepository.save(cart);
    }
    else {
        // ONLINE
        paymentUrl = paymentService.createPaymentUrl(order);
    }

    OrderResponse response = new OrderResponse();

    response.setOrderId(order.getId());

    response.setOrderCode(order.getOrderCode());

    response.setTotalPrice(order.getTotalPrice());

    response.setPaymentUrl(paymentUrl);

    response.setStatus(order.getStatus().name());

    return response;
}

    @Override
    public List<MyOrderResponse> getMyOrders() {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<MyOrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {

            MyOrderResponse response = new MyOrderResponse();

            response.setOrderId(order.getId());

            response.setOrderCode(order.getOrderCode());

            response.setStatus(order.getStatus().name());

            response.setTotalPrice(order.getTotalPrice());

            response.setCreatedAt(order.getCreatedAt());

            response.setPaymentMethod(order.getPaymentMethod().getCode().name());

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
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus().name());
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
                .orElseThrow(() ->
                        new ApplicationException("Tài khoản không tồn tại"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        for (OrderDetail detail : order.getOrderDetails()) {

            Optional<CartItem> existingItem = cartItemRepository.findByCart_IdAndFood_Id(cart.getId(),
                            detail.getFood().getId());

            if (existingItem.isPresent()) {

                CartItem item = existingItem.get();

                item.setQuantity(item.getQuantity() + detail.getQuantity());

                cartItemRepository.save(item);

            } else {

                CartItem item = new CartItem();

                item.setCart(cart);
                item.setFood(detail.getFood());
                item.setQuantity(detail.getQuantity());

                cartItemRepository.save(item);
            }
        }
    }
}

