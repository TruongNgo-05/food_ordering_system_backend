package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.admin.BannerCreateAndUpdateRequest;
import com.example.project_back.dto.request.admin.CategoriesCreateAndUpdate;
import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.request.user.table.CreateAndUpdateTableRequest;
import com.example.project_back.dto.response.admin.BannerAdminResponse;
import com.example.project_back.dto.response.admin.VoucherAdminDetailResponse;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.dto.response.user.CategoriesResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.service.BannerService;
import com.example.project_back.service.CategoriesService;
import com.example.project_back.service.TableService;
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
@RequestMapping("api/admin/")
public class AdminController {

    private final VoucherService  voucherService;
    private final TableService tableService;
    private final CategoriesService categoriesService;
    private final BannerService bannerService;

    //    voucher
    @GetMapping("voucher")
    public ResponseEntity<BaseResponse<Page<VoucherAdminResponse>>> getVouchers(
            VoucherRequestParam param,@PageableDefault(size = 5 , sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVouchers(param,pageable),
                "get All vouchers successfully"
        ));
    }

    @GetMapping("voucher/{voucherId}")
    public ResponseEntity<BaseResponse<VoucherAdminDetailResponse>> getVoucherById(@PathVariable Integer voucherId) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVoucherById(voucherId),
                "get id vouchers successfully"
        ));
    }

    @PostMapping("voucher")
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> createVoucher(@RequestBody @Valid VoucherCreateAndUpdateRequest create ){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.createVoucher(create),
                "create vouchers successfully"
        ));
    }

    @PutMapping("voucher/{id}")
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> updateVoucher(@PathVariable Integer id,@RequestBody @Valid VoucherCreateAndUpdateRequest update){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.updateVoucher(id,update),
                "update vouchers successfully"
        ));
    }

    @DeleteMapping("voucher/{id}")
    public ResponseEntity<BaseResponse<String>> deleteVoucher(@PathVariable Integer id){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.deleteVoucher(id),
                "delete vouchers successfully"
        ));
    }


    //    table
    @PostMapping("/table")
    public ResponseEntity<BaseResponse<TableResponse>> createTable(@RequestBody CreateAndUpdateTableRequest create) {
        return ResponseEntity.ok(new BaseResponse<>(
                tableService.createTable(create),
                "create table successfully!"
        ));
    }

    @PutMapping("/table/{id}")
    public ResponseEntity<BaseResponse<TableResponse>> updateTable(@RequestBody CreateAndUpdateTableRequest update, @PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                tableService.updateTable(update, id),
                "update table successfully!"
        ));
    }

    @DeleteMapping("/table/{id}")
    public ResponseEntity<BaseResponse<String>> deleteTable(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                tableService.deleteTable(id),
                "delete table successfully!"
        ));
    }


    // categories
    @PostMapping("categories")
    public ResponseEntity<BaseResponse<CategoriesResponse>> createCategories(@RequestBody CategoriesCreateAndUpdate create) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.createCategories(create),
                "Create Categories Admin successfully!"
        ));
    }

    @PutMapping("categories/{id}")
    public ResponseEntity<BaseResponse<CategoriesResponse>> updateCategories(@RequestBody CategoriesCreateAndUpdate update, @PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.updateCategories(update, id),
                "Update Categories Admin successfully!"
        ));
    }

    @DeleteMapping("categories{id}")
    public ResponseEntity<BaseResponse<String>> deleteCategories(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.deleteCategories(id),
                "Delete Categories Admin successfully!"
        ));
    }


    //    banner
    @GetMapping("banner")
    public ResponseEntity<BaseResponse<Page<BannerAdminResponse>>> getAllBannerAdmin(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.getAllBannerAdmin(pageable),
                "get All Banner Admin successfully!"
        ));
    }

    @PostMapping("banner")
    public ResponseEntity<BaseResponse<BannerAdminResponse>> createBanner(@RequestBody @Valid BannerCreateAndUpdateRequest createDto) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.createBanner(createDto),
                "Create Banner successfully!"
        ));
    }

    @PutMapping("banner/{id}")
    public ResponseEntity<BaseResponse<BannerAdminResponse>> updateBanner(@RequestBody @Valid BannerCreateAndUpdateRequest updateDto, @PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.updateBanner(updateDto, id),
                "Update Banner successfully!"
        ));
    }

    @DeleteMapping("banner/{id}")
    public ResponseEntity<BaseResponse<String>> deleteBanner(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.deleteBanner(id),
                "Delete Banner successfully!"
        ));
    }
}
