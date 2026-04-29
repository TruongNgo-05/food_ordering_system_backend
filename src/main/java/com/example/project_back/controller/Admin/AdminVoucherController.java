package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/admin/voucher")
public class AdminVoucherController {

    private final VoucherService  voucherService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<VoucherAdminResponse>>> getVouchers(
            VoucherRequestParam param,@PageableDefault(size = 5 , sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVouchers(param,pageable),
                "get All vouchers successfully"
        ));
    }

    @GetMapping("{voucherId}")
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> getVoucherById(@PathVariable Integer voucherId) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVoucherById(voucherId),
                "get id vouchers successfully"
        ));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> createVoucher(@RequestBody @Valid VoucherCreateAndUpdateRequest create ){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.createVoucher(create),
                "create vouchers successfully"
        ));
    }

    @PutMapping("{id}")
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> updateVoucher(@PathVariable Integer id,@RequestBody @Valid VoucherCreateAndUpdateRequest update){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.updateVoucher(id,update),
                "update vouchers successfully"
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteVoucher(@PathVariable Integer id){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.deleteVoucher(id),
                "delete vouchers successfully"
        ));
    }
}
