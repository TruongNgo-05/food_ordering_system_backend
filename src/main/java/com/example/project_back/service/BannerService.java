package com.example.project_back.service;

import com.example.project_back.dto.request.admin.BannerCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.BannerAdminResponse;
import com.example.project_back.dto.response.custommer.BannerCustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BannerService {
    //   user
    List<BannerCustomerResponse> getAllBannerCustomer();

    // admin
    Page<BannerAdminResponse> getAllBannerAdmin(Pageable pageable);

    BannerAdminResponse createBanner(BannerCreateAndUpdateRequest create);

    BannerAdminResponse updateBanner(BannerCreateAndUpdateRequest update, Integer id);

    String deleteBanner(Integer id);
}
