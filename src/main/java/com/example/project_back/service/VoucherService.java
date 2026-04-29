package com.example.project_back.service;

import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {
    Page<VoucherAdminResponse> getVouchers(VoucherRequestParam param, Pageable pageable);

    VoucherAdminResponse getVoucherById(Integer id);

    VoucherAdminResponse createVoucher(VoucherCreateAndUpdateRequest create);

    VoucherAdminResponse updateVoucher(Integer id, VoucherCreateAndUpdateRequest update);

    String deleteVoucher(Integer id);
}
