package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.admin.BannerCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.BannerAdminResponse;
import com.example.project_back.service.BannerService;
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
@RequestMapping("api/admin/banner")
public class AdminBannerController {
    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<BannerAdminResponse>>>  getAllBannerAdmin(@PageableDefault(size =5 ,sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.getAllBannerAdmin(pageable),
                "get All Banner Admin successfully!"
        ));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<BannerAdminResponse>> createBanner(@RequestBody @Valid BannerCreateAndUpdateRequest createDto) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.createBanner(createDto),
                "Create Banner successfully!"
        ));
    }

    @PutMapping("{id}")
    public ResponseEntity<BaseResponse<BannerAdminResponse>> updateBanner(@RequestBody @Valid BannerCreateAndUpdateRequest updateDto,@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.updateBanner(updateDto,id),
                "Update Banner successfully!"
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteBanner(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.deleteBanner(id),
                "Delete Banner successfully!"
        ));
    }
}
