package com.example.project_back.utils;

import com.example.project_back.entity.Voucher;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.VoucherRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@AllArgsConstructor
public class VoucherValidator {
    private VoucherRepository voucherRepository;

    public Voucher validateVoucher(String voucherCode, Double total ) {
        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new ApplicationException( "Voucher không tồn tại" ));
        LocalDateTime now = LocalDateTime.now();
        // start date
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new ApplicationException( "Voucher chưa bắt đầu" );
        }
        // end date
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new ApplicationException( "Voucher đã hết hạn" );
        }
        // min order value
        if (voucher.getMinOrderValue() != null && total < voucher.getMinOrderValue()) {
            throw new ApplicationException( "Chưa đủ giá trị đơn hàng" );
        }
        // usage limit
        int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
        Integer limit = voucher.getUsageLimit();
        if (limit != null && limit > 0 && used >= limit) {
            throw new ApplicationException( "Voucher đã hết lượt" );
        }
        return voucher;
    }

    public Double calculateDiscount(
            Voucher voucher,
            Double total
    ) {

        double discount = voucher.getDiscount();

        if (discount > total) {
            discount = total;
        }

        return discount;
    }

}
