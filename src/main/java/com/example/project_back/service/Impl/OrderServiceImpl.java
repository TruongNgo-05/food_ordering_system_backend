    package com.example.project_back.service.Impl;

    import com.example.project_back.config.SecurityUtils;
    import com.example.project_back.constant.OrderStatus;
    import com.example.project_back.constant.PaymentMethodType;
    import com.example.project_back.constant.PaymentStatus;
    import com.example.project_back.constant.TableStatus;
    import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
    import com.example.project_back.dto.request.customer.order.CreateOrderTableRequest;
    import com.example.project_back.dto.request.customer.order.OrderTableItemRequest;
    import com.example.project_back.dto.request.spec.OrderRequestParam;
    import com.example.project_back.dto.response.admin.OrderAdminResponse;
    import com.example.project_back.dto.response.staff.OrderStaffOffLineResponse;
    import com.example.project_back.dto.response.staff.OrderStaffOnLineResponse;
    import com.example.project_back.dto.response.staff.OrderDetailStaffResponse;
    import com.example.project_back.dto.response.customer.order.OrderResponse;
    import com.example.project_back.dto.response.customer.order.MyOrderResponse;
    import com.example.project_back.dto.response.customer.order.OrderDetailResponse;
    import com.example.project_back.dto.response.customer.order.OrderTableResponse;
    import com.example.project_back.entity.*;
    import com.example.project_back.exception.ApplicationException;
    import com.example.project_back.mapper.OrderMapper;
    import com.example.project_back.repository.*;
    import com.example.project_back.service.OrderService;
    import com.example.project_back.service.SepayService;
    import com.example.project_back.specification.OrderSpecification;
    import com.example.project_back.specification.OrderSpecificationBuilder;
    import com.example.project_back.utils.OrderStatusValidator;
    import com.example.project_back.utils.VoucherValidator;
    import jakarta.transaction.Transactional;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.domain.Specification;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
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
        private final UserRepository userRepository;
        private final VoucherValidator voucherValidator;
        private final TableDetailRepository tableDetailRepository;
        private final FoodRepository foodRepository;
        private final SepayService sepayService;

        @Override
        @Transactional
        public OrderResponse createOrder(CreateOrderRequest request) {

            String username = SecurityUtils.getCurrentUsername();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApplicationException("User không tồn tại"));

            Cart cart = cartRepository.findByUser_Id(user.getId())
                    .orElseThrow(() -> new ApplicationException("Cart không tồn tại"));

            if (cart.getItems().isEmpty()) {
                throw new ApplicationException("Cart trống");
            }

            UserAddress address = userAddressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new ApplicationException("Địa chỉ không tồn tại"));

            PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new ApplicationException("Payment method không tồn tại"));

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

            Order order;

            if (paymentMethod.getCode() == PaymentMethodType.COD) {

                order = OrderMapper.toCodOrder(user, address, paymentMethod, voucher, request, total, discount);

            } else {

                order = OrderMapper.toOrder(user, address, paymentMethod, voucher, request, total, discount);
            }

            // order details
            List<OrderDetail> details = OrderMapper.toOrderDetails(order, cart.getItems());
            order.setOrderDetails(details);
            orderRepository.save(order);

            // payment
            Optional<Payment> existingPayment =
                    paymentRepository.findByOrderId(order.getId());

            if (existingPayment.isEmpty()) {
                Payment payment = OrderMapper.toPayment(order, paymentMethod);
                paymentRepository.save(payment);
            }

            String paymentUrl = null;

            // COD
            if (paymentMethod.getCode() == PaymentMethodType.COD) {

                order.setStatus(OrderStatus.CONFIRMED);

                cart.getItems().clear();

                cartRepository.save(cart);

                orderRepository.save(order);
            }

// ONLINE -> QR BANK
            else if (paymentMethod.getCode() == PaymentMethodType.ONLINE) {

                paymentUrl = sepayService.generateQr(
                        order.getOrderCode(),
                        order.getTotalPrice() - order.getDiscount()
                );
            }
            return OrderMapper.toOrderResponse(order, paymentUrl);
        }

        @Override
        public Page<MyOrderResponse> getMyOrders(OrderRequestParam param, Pageable pageable) {
            String username = SecurityUtils.getCurrentUsername();

            if (username == null || username.equals("anonymousUser")) {
                throw new ApplicationException("Bạn chưa đăng nhập");
            }

            // tìm user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApplicationException("User không tồn tại"));

            // lấy params filter
            String orderCode = param.getOrderCode();
            OrderStatus status = param.getStatus();
            LocalDate minDate = param.getMinDate();
            LocalDate maxDate = param.getMaxDate();

            // tạo specification
            Specification<Order> spec = Specification.unrestricted();

            // chỉ lấy order của user hiện tại
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("user").get("id"), user.getId())
            );

            // filter order code
            if (orderCode != null && !orderCode.trim().isEmpty()) {
                spec = spec.and(OrderSpecification.hasOrderCode(orderCode));
            }

            // filter status
            if (status != null) {
                spec = spec.and(OrderSpecification.hasOrderStatus(status));
            }

            // filter date
            if (minDate != null && maxDate != null) {
                spec = spec.and(OrderSpecification.hasCreateDate(minDate, maxDate));
            }
            Page<Order> orderPage = orderRepository.findAll(spec, pageable);

            Page<MyOrderResponse> responsePage = orderPage.map(order -> {

                Payment payment = paymentRepository
                        .findByOrderId(order.getId())
                        .orElse(null);
                return OrderMapper.toMyOrderResponse(order, payment);
            });

            return responsePage;
        }

        @Override
        public OrderDetailResponse getOrderDetail(Long orderId) {
            String username = SecurityUtils.getCurrentUsername();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApplicationException("Tài khoản không tồn tại"));

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ApplicationException("Order not found"));
            if (!order.getUser().getId().equals(user.getId())) {
                throw new ApplicationException("Bạn không có quyền xem đơn hàng này");
            }

            Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            return OrderMapper.toOrderDetailResponse(order, payment);
        }

        @Override
        @Transactional
        public void cancelOrder(Long orderId) {
            String username = SecurityUtils.getCurrentUsername();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new ApplicationException("Tài khoản không tồn tại"));

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ApplicationException("Order not found"));

            // kiểm tra chủ sở hữu order
            if (!order.getUser().getId().equals(user.getId())) {

                throw new ApplicationException(
                        "Bạn không có quyền xem đơn hàng này"
                );
            }

            order.setStatus(OrderStatus.CANCELED);
            order.setUpdatedAt(LocalDateTime.now());

            orderRepository.save(order);
        }

        @Override
        @Transactional
        public void reorder(Long orderId) {

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

                Optional<CartItem> existingItem =
                        cartItemRepository.findByCart_IdAndFood_Id(cart.getId(), detail.getFood().getId());

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

        @Override
        @Transactional
        public OrderTableResponse createOrderTb(
                CreateOrderTableRequest request
        ) {

            // TABLE
            TableDetail table = tableDetailRepository.findByTableNumber(request.getTableNumber())
                    .orElseThrow(() -> new ApplicationException("Bàn không tồn tại"));

            table.setStatus(TableStatus.OCCUPIED);

            tableDetailRepository.save(table);

            // PAYMENT METHOD
            PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() ->
                            new ApplicationException("Payment method không tồn tại"));

            // TOTAL
            Double total = 0.0;

            List<Food> foods = new ArrayList<>();

            for (OrderTableItemRequest item : request.getItems()) {

                Food food = foodRepository.findById(item.getFoodId())
                        .orElseThrow(() ->
                                new ApplicationException("Món ăn không tồn tại"));

                foods.add(food);

                total += food.getPrice() * item.getQuantity();
            }

            // CREATE ORDER
            Order order = OrderMapper.toTableOrder(request, table, paymentMethod, total);

            order = orderRepository.save(order);

            // ORDER DETAILS
            List<OrderDetail> details = OrderMapper.toTableOrderDetails(order, foods, request.getItems());

            order.setOrderDetails(details);

            orderRepository.save(order);

            // PAYMENT
            Payment payment = OrderMapper.toPayment(order, paymentMethod);

            payment.setStatus(PaymentStatus.PENDING);

            String paymentUrl = null;

            // ONLINE
            if (paymentMethod.getCode() == PaymentMethodType.ONLINE) {

                paymentUrl = sepayService.generateQr(order.getOrderCode(), total);
            }

            paymentRepository.save(payment);

            return OrderMapper.toOrderTableResponse(order, paymentUrl);
        }


//staff

        @Override
        public Page<OrderStaffOnLineResponse> getOnlineOrders(
                OrderRequestParam param,
                Pageable pageable
        ) {

            Specification<Order> spec = OrderSpecificationBuilder.build(param)
                    .and(OrderSpecification.isOnlineOrCodOrder())
                    .and(OrderSpecification.hasCreatedAtBetween(param.getMinDate(), param.getMaxDate()))
                    .and(OrderSpecification.hasStatus(param.getStatus()))
                    .and(OrderSpecification.orderByStatusPriority());

            return orderRepository.findAll(spec, pageable)
                    .map(order -> OrderMapper.toOnlineResponse(
                            order,
                            paymentRepository
                                    .findByOrderId(order.getId())
                                    .orElse(null)
                    ));
        }

        @Override
        public Page<OrderStaffOffLineResponse> getTableOrders(
                OrderRequestParam param,
                Pageable pageable
        ) {

            Specification<Order> spec =
                    OrderSpecificationBuilder.build(param)
                            .and(OrderSpecification.isTableOrder())
                            .and(OrderSpecification.hasCreatedAtBetween(param.getMinDate(), param.getMaxDate()))
                            .and(OrderSpecification.hasStatus(param.getStatus()))
                             .and(OrderSpecification.orderByStatusPriority());

            return orderRepository.findAll(spec, pageable)
                    .map(order -> OrderMapper.toOfflineResponse(
                            order,
                            paymentRepository
                                    .findByOrderId(order.getId())
                                    .orElse(null)
                    ));
        }

        @Override
        public OrderDetailStaffResponse getOrderStaffDetail(
                Long orderId
        ) {

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ApplicationException("Order không tồn tại"));

            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElse(null);

            return OrderMapper.toStaffDetailResponse(order, payment);
        }
        @Override
        public void updateStatus(Long orderId, OrderStatus status) {

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ApplicationException("Order không tồn tại"));

            if (!OrderStatusValidator.isValidTransition(order, status)) {
                throw new ApplicationException("Không thể chuyển trạng thái này");
            }

            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());

            if (status == OrderStatus.COMPLETED && order.getTable() != null) {
                TableDetail table = order.getTable();
                table.setStatus(TableStatus.AVAILABLE);
                tableDetailRepository.save(table);
            }

            orderRepository.save(order);
        }


//        admin
@Override
public Page<OrderAdminResponse> getAllAdminOrders(
        OrderRequestParam param,
        Pageable pageable
) {

    Specification<Order> spec = OrderSpecificationBuilder.build(param);

    return orderRepository.findAll(spec, pageable)
            .map(order -> {
                Payment payment = paymentRepository.findByOrderId(order.getId())
                        .orElse(null);

                return OrderMapper.toAdminResponse(order, payment);
            });
}
    }

