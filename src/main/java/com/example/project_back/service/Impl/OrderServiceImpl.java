package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.response.customer.OrderCheckResponse;
import com.example.project_back.entity.Cart;
import com.example.project_back.entity.CartItem;
import com.example.project_back.entity.User;
import com.example.project_back.entity.Voucher;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.CartRepository;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class OrderServiceImpl {
private OrderRepository orderRepository;
private UserRepository userRepository;
private VoucherRepository voucherRepository;
private CartRepository cartRepository;
    //    check pice
    @Transactional
    public OrderCheckResponse checkDiscount(String voucherCode) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User không tồn tại"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ApplicationException("Cart không tồn tại"));

        //  tổng tiền giỏ hàng
        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            total += item.getFood().getPrice() * item.getQuantity();
        }

        //  CASE KHÔNG DÙNG VOUCHER
        if (voucherCode == null || voucherCode.trim().isEmpty()) {

            OrderCheckResponse res = new OrderCheckResponse();
            res.setDescription("Không áp dụng voucher");
            res.setDiscount(0.0);
            res.setMinOrderValue(0.0);
            res.setTotalBefore(total);
            res.setTotalAfter(total);

            return res;
        }

        //  có voucher thì xử lý bình thường
        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy voucher"));

        LocalDateTime now = LocalDateTime.now();

        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new ApplicationException("Voucher chưa bắt đầu");
        }

        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new ApplicationException("Voucher đã hết hạn");
        }

        if (voucher.getMinOrderValue() != null && total < voucher.getMinOrderValue()) {
            throw new ApplicationException("Chưa đủ giá trị đơn hàng");
        }

        int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
        Integer limit = voucher.getUsageLimit();

        if (limit != null && limit > 0 && used >= limit) {
            throw new ApplicationException("Voucher đã hết lượt");
        }

        //  giảm tiền
        double discountAmount = voucher.getDiscount();
        if (discountAmount > total) {
            discountAmount = total;
        }

        double totalAfter = total - discountAmount;

        OrderCheckResponse res = new OrderCheckResponse();
        res.setDescription(voucher.getDescription());
        res.setMinOrderValue(voucher.getMinOrderValue());
        res.setTotalBefore(total);
        res.setDiscount(discountAmount);
        res.setTotalAfter(totalAfter);

        return res;
    }

    @Transactional
    public OrderCheckResponse usedDiscount(String voucherCode) {

        OrderCheckResponse res = checkDiscount(voucherCode);

        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy voucher"));

        int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
        Integer limit = voucher.getUsageLimit();

        if (limit != null && limit > 0 && used >= limit) {
            throw new ApplicationException("Voucher đã hết lượt");
        }

        voucher.setUsedCount(used + 1);
        voucherRepository.save(voucher);

        return res;
    }
}
