package com.example.project_back.service.Impl;

import com.example.project_back.dto.request.admin.BannerCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.BannerAdminResponse;
import com.example.project_back.dto.response.user.BannerResponse;
import com.example.project_back.entity.Banner;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.BannerMapper;
import com.example.project_back.repository.BannerRespository;
import com.example.project_back.service.BannerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRespository bannerRespository;

    //   user
    @Override
    public List<BannerResponse> getAllBannerCustomer() {
        List<Banner> banners = bannerRespository.findAll();
        List<BannerResponse> dto = new ArrayList<>();
        for (Banner banner : banners) {
            if(banner.getIsActive()==true){
            dto.add(BannerMapper.toCustomerResponse(banner));}
        }
        return dto;
    }

    // admin
    @Override
    public Page<BannerAdminResponse> getAllBannerAdmin(Pageable pageable) {
        Page<Banner> banners = bannerRespository.findAll(pageable);
        return banners.map(BannerMapper::toAdminResponse);
    }

    @Transactional
    @Override
    public BannerAdminResponse createBanner(BannerCreateAndUpdateRequest create){
        if(bannerRespository.existsByImageUrl(create.getImageUrl())){
            throw new ApplicationException(" ảnh đã tồn tại ");
        }
        Banner banner = BannerMapper.toEntity(create);
        Banner SavedBanner = bannerRespository.save(banner);
        BannerAdminResponse adminResponse = BannerMapper.toAdminResponse(SavedBanner);
        return adminResponse;
    }

    @Transactional
    @Override
    public BannerAdminResponse updateBanner(BannerCreateAndUpdateRequest update, Integer id){
        Optional<Banner> bannerOptional = bannerRespository.findById(id);
        if(bannerOptional.isEmpty()){
            throw new ApplicationException("k tim thay ảnh");
        }
        if(bannerRespository.existsByImageUrlAndIdNot(update.getImageUrl(),id)){
            throw new ApplicationException("ảnh đã tồn tại");
        }
        Banner banner = bannerOptional.get();
        BannerMapper.updateEntity(update,banner);
        return  BannerMapper.toAdminResponse(bannerRespository.save(banner));
    }

    @Transactional
    public String deleteBanner(Integer id){
        Optional<Banner> bannerOptional = bannerRespository.findById(id);
       if(bannerOptional.isEmpty()){
           throw new ApplicationException("k tim thay ");
       }
        bannerRespository.delete(bannerOptional.get());
        return "delete success";
    }

}
