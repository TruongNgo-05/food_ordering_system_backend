package com.example.project_back.mapper;

import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.entity.Voucher;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Locale;

public class VoucherMapper {
    public static VoucherAdminResponse toVoucherAdminResponse(Voucher voucher){
        VoucherAdminResponse voucherAdminResponse = new VoucherAdminResponse();
        BeanUtils.copyProperties(voucher, voucherAdminResponse);
        voucherAdminResponse.setVoucherCode(voucher.getCode());
        return voucherAdminResponse;
    }

    public static Voucher toVoucherAdminCreateResponse(VoucherCreateAndUpdateRequest create){
        Voucher voucher = new Voucher();

        voucher.setCode(create.getVoucherCode().trim().toUpperCase(Locale.ROOT));
        voucher.setDescription(create.getDescription());
        voucher.setDiscount(create.getDiscount());
        voucher.setMinOrderValue(create.getMinOrderValue());

        voucher.setUsageLimit(create.getUsageLimit());
        voucher.setUsedCount(0);

        voucher.setStartDate(create.getStartDate());
        voucher.setEndDate(create.getEndDate());

        voucher.setCreatedAt(LocalDateTime.now());
        return voucher;
    }
    public static void toVoucherAdminUpdateResponse(VoucherCreateAndUpdateRequest update,Voucher voucher){

        if(update.getVoucherCode()!=null){
            voucher.setCode(update.getVoucherCode());
        }
        if(update.getDiscount()!=null){
            voucher.setDiscount(update.getDiscount());
        }
        if(update.getMinOrderValue()!=null){
        voucher.setMinOrderValue(update.getMinOrderValue());
        }
        if(update.getUsageLimit()!=null){
            voucher.setUsageLimit(update.getUsageLimit());
        }
    }
}
