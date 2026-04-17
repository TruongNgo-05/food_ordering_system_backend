package com.example.project_back.mapper;

import com.example.project_back.dto.request.admin.BannerCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.BannerAdminResponse;
import com.example.project_back.dto.response.custommer.BannerCustomerResponse;
import com.example.project_back.entity.Banner;
import org.springframework.beans.BeanUtils;

public class BannerMapper {
    public static BannerCustomerResponse toCustomerResponse(Banner banner) {
        BannerCustomerResponse dto = new BannerCustomerResponse();
        BeanUtils.copyProperties(banner,dto);
        return dto;
    }


//    admin
    public static BannerAdminResponse toAdminResponse(Banner banner){
        BannerAdminResponse dto = new BannerAdminResponse();
        BeanUtils.copyProperties(banner,dto);
        return dto;
    }

    public static Banner toEntity (BannerCreateAndUpdateRequest dto) {
        Banner  banner = new Banner();
        BeanUtils.copyProperties(dto,banner);
        banner.setIsActive(true);
        return banner;
    }

    public static void updateEntity(BannerCreateAndUpdateRequest dto, Banner banner){
        if(dto.getTitle()!=null){
            banner.setTitle(dto.getTitle());
        }
        if(dto.getDescription()!=null){
            banner.setDescription(dto.getDescription());
        }
        if(dto.getImageUrl()!=null){
            banner.setImageUrl(dto.getImageUrl());
        }
        if(dto.getIsActive()!=null){
            banner.setIsActive(dto.getIsActive());
        }
    }
}
