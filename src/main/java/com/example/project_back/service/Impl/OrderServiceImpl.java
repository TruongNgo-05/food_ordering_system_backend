package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentMethodType;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.response.customer.order.OrderResponse;
import com.example.project_back.dto.response.customer.order.MyOrderResponse;
import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
import com.example.project_back.dto.response.customer.order.OrderItemResponse;
import com.example.project_back.entity.*;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.OrderMapper;
import com.example.project_back.repository.*;
import com.example.project_back.service.OrderService;
import com.example.project_back.service.PaymentService;
import com.example.project_back.validator.VoucherValidator;
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
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final VoucherRepository voucherRepository;
    private final UserAddressRepository userAddressRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final VoucherValidator voucherValidator;

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

        UserAddress address = userAddressRepository.findById(request.getAddressId())
                .orElseThrow(() ->
                        new ApplicationException("Địa chỉ không tồn tại"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() ->
                        new ApplicationException("Payment method không tồn tại"));

        // tổng tiền gốc
        Double total = 0.0;

        for (CartItem item : cart.getItems()) {

            Double itemTotal = item.getFood().getPrice() * item.getQuantity();

            total += itemTotal;
        }

        // voucher
        Double discount = 0.0;

        Voucher voucher = null;

        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {

            voucher = voucherValidator.validateVoucher(request.getVoucherCode(), total);

            discount = voucherValidator.calculateDiscount(voucher, total);

            int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();

            voucher.setUsedCount(used + 1);

            voucherRepository.save(voucher);
        }

        // tiền sau giảm
        Double totalAfter = total - discount;

        // tạo order
        Order order = new Order();

        order.setOrderCode("ORD-" + System.currentTimeMillis());

        order.setUser(user);

        order.setAddress(address);

        order.setPaymentMethod(paymentMethod);

        order.setStatus(OrderStatus.PENDING);

        // giá cuối cùng
        order.setTotalPrice(total);

        order.setDiscount(discount);

        order.setCreatedAt(LocalDateTime.now());

        order.setUpdatedAt(LocalDateTime.now());

        order.setNote(request.getNote());

        order.setVoucher(voucher);

        order = orderRepository.save(order);

        // order details
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

        // payment
        Payment payment = new Payment();

        payment.setOrder(order);

        payment.setPaymentMethod(paymentMethod);

        payment.setCreatedAt(LocalDateTime.now());

        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        String paymentUrl = null;

        // COD
        if (paymentMethod.getCode() == PaymentMethodType.COD) {

            cart.getItems().clear();

            cartRepository.save(cart);

        } else {

            paymentUrl = paymentService.createPaymentUrl(order);
        }

        return OrderMapper.toOrderResponse(
                order,
                totalAfter,
                paymentUrl
        );
    }

    @Override
    public List<MyOrderResponse> getMyOrders() {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Optional<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            throw new ApplicationException("User không tồn tại");
        }
        User user = users.get();

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<MyOrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(OrderMapper.toMyOrderResponse(order));
        }

        return responses;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        return OrderMapper.toOrderDetailResponse(
                order,
                payment
        );
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

