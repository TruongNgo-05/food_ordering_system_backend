package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.response.customer.OrderCheckResponse;
import com.example.project_back.dto.response.customer.order.CreateOrderResponse;
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
    //    check pice
//    @Transactional
//    public OrderCheckResponse checkDiscount(String voucherCode) {
//
//        String username = SecurityUtils.getCurrentUsername();
//
//        if (username == null || username.equals("anonymousUser")) {
//            throw new ApplicationException("Bạn chưa đăng nhập");
//        }
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ApplicationException("User không tồn tại"));
//
//        Cart cart = cartRepository.findByUser_Id(user.getId())
//                .orElseThrow(() -> new ApplicationException("Cart không tồn tại"));
//
//        //  tổng tiền giỏ hàng
//        double total = 0.0;
//        for (CartItem item : cart.getItems()) {
//            total += item.getFood().getPrice() * item.getQuantity();
//        }
//
//        //  CASE KHÔNG DÙNG VOUCHER
//        if (voucherCode == null || voucherCode.trim().isEmpty()) {
//
//            OrderCheckResponse res = new OrderCheckResponse();
//            res.setDescription("Không áp dụng voucher");
//            res.setDiscount(0.0);
//            res.setMinOrderValue(0.0);
//            res.setTotalBefore(total);
//            res.setTotalAfter(total);
//
//            return res;
//        }
//
//        //  có voucher thì xử lý bình thường
//        Voucher voucher = voucherRepository.findByCode(voucherCode)
//                .orElseThrow(() -> new ApplicationException("Không tìm thấy voucher"));
//
//        LocalDateTime now = LocalDateTime.now();
//
//        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
//            throw new ApplicationException("Voucher chưa bắt đầu");
//        }
//
//        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
//            throw new ApplicationException("Voucher đã hết hạn");
//        }
//
//        if (voucher.getMinOrderValue() != null && total < voucher.getMinOrderValue()) {
//            throw new ApplicationException("Chưa đủ giá trị đơn hàng");
//        }
//
//        int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
//        Integer limit = voucher.getUsageLimit();
//
//        if (limit != null && limit > 0 && used >= limit) {
//            throw new ApplicationException("Voucher đã hết lượt");
//        }
//
//        //  giảm tiền
//        double discountAmount = voucher.getDiscount();
//        if (discountAmount > total) {
//            discountAmount = total;
//        }
//
//        double totalAfter = total - discountAmount;
//
//        OrderCheckResponse res = new OrderCheckResponse();
//        res.setDescription(voucher.getDescription());
//        res.setMinOrderValue(voucher.getMinOrderValue());
//        res.setTotalBefore(total);
//        res.setDiscount(discountAmount);
//        res.setTotalAfter(totalAfter);
//
//        return res;
//    }
//
//    @Transactional
//    public OrderCheckResponse usedDiscount(String voucherCode) {
//
//        OrderCheckResponse res = checkDiscount(voucherCode);
//
//        Voucher voucher = voucherRepository.findByCode(voucherCode)
//                .orElseThrow(() -> new ApplicationException("Không tìm thấy voucher"));
//
//        int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
//        Integer limit = voucher.getUsageLimit();
//
//        if (limit != null && limit > 0 && used >= limit) {
//            throw new ApplicationException("Voucher đã hết lượt");
//        }
//
//        voucher.setUsedCount(used + 1);
//        voucherRepository.save(voucher);
//
//        return res;
//    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart empty");
        }

        double total = 0;

        Order order = new Order();

        order.setOrderCode("ORD-" + System.currentTimeMillis());

        order.setUser(user);

        order.setCustomerName(request.getCustomerName());

        order.setCustomerPhone(request.getCustomerPhone());

        order.setAddressId(request.getAddressId());

        order.setTableId(request.getTableId());

        order.setNote(request.getNote());

        order.setStatus(OrderStatus.PENDING);

        order.setCreatedAt(LocalDateTime.now());

        PaymentMethod paymentMethod =
                paymentMethodRepository.findById(request.getPaymentMethodId())
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

        for (CartItem item : cart.getCartItems()) {

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

        Payment payment = new Payment();

        payment.setOrder(order);

        payment.setPaymentMethod(paymentMethod);

        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        cartItemRepository.deleteAll(cart.getCartItems());

        CreateOrderResponse response = new CreateOrderResponse();

        response.setOrderId(order.getId());

        response.setOrderCode(order.getOrderCode());

        response.setTotalPrice(total);

        response.setStatus(order.getStatus().name());

        if (paymentMethod.getCode() == PaymentMethodType.ONLINE) {

            String paymentUrl = vnPayService.createPaymentUrl(order);

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

        response.setPaymentMethod(
                order.getPaymentMethod().getCode().name()
        );

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElse(null);

        if (payment != null) {

            response.setPaymentStatus(
                    payment.getStatus().name()
            );
        }

        if (order.getAddressId() != null) {

            UserAddress address =
                    userAddressRepository.findById(order.getAddressId())
                            .orElse(null);

            if (address != null) {
                response.setAddress(address.getAddress());
            }
        }

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
        }

        response.setItems(itemResponses);

        return response;
    }

    @Override
    public void cancelOrder(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.CANCELED);

        orderRepository.save(order);
    }

    @Override
    public void reorder(Integer orderId) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
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

