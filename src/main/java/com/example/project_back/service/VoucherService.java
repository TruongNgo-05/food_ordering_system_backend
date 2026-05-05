package com.example.project_back.service;

import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.VoucherAdminDetailResponse;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.dto.response.customer.voucher.VoucherGetResponse;
import com.example.project_back.dto.response.customer.voucher.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoucherService {
    //admin
    Page<VoucherAdminResponse> getVouchers(VoucherRequestParam param, Pageable pageable);

    VoucherAdminDetailResponse getVoucherById(Integer id);

    VoucherAdminResponse createVoucher(VoucherCreateAndUpdateRequest create);

    VoucherAdminResponse updateVoucher(Integer id, VoucherCreateAndUpdateRequest update);

    String deleteVoucher(Integer id);
//    customer
VoucherResponse checkVoucherCode(String voucherCode);
    List<VoucherGetResponse> getVoucherCustomer();
//    OrderCheckResponse checkDiscount(String voucherCode);
//OrderCheckResponse usedDiscount(String voucherCode);
}
