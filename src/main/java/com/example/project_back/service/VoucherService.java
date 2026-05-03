package com.example.project_back.service;

import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.VoucherAdminDetailResponse;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.dto.response.customer.VoucherGetResponse;
import com.example.project_back.dto.response.customer.VoucherResponse;
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
    List<VoucherGetResponse> getVoucherCustomer();
    VoucherResponse checkVoucher(String voucherCode);
VoucherResponse usedVoucher(String voucherCode);
}
