package com.example.project_back.repository;

import com.example.project_back.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRespository extends JpaRepository<Banner,Integer> {
    boolean existsByImageUrl(String imageUrl);

//    Kiểm tra có banner nào cùng imageUrl Nhưng khác id hiện tại
    boolean existsByImageUrlAndIdNot(String imageUrl, Integer id);

}
